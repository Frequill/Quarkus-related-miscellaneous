/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.runtime.Startup;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 *
 * @author flax
 */
@Startup
@ApplicationScoped // Force creation on startup
public class BackendEmulator {

    public static final Logger LOG = Logger.getLogger(BackendEmulator.class);

    ReactiveListCommands<String, RequestEntity> requests;
    ReactiveListCommands<String, ResponseEntity> responses;

    @ConfigProperty(name = "demo.backend.backendQueue", defaultValue = "requests")
    String backendQueueName;
    @ConfigProperty(name = "demo.backend.poptimeout", defaultValue = "10")
    Long blpoptimeout;

    public BackendEmulator(ReactiveRedisDataSource ds) {

        requests = ds.list(RequestEntity.class);
        responses = ds.list(ResponseEntity.class);
        LOG.info("Setting up list listener");
        try {
            Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> {
                try {
                    return requests.blpop(Duration.ofSeconds(10), "requests");
                } catch (Exception ex) {
                    return null;
                }
            }).indefinitely()
                    //.invoke((stuff) -> LOG.info("Got " + stuff.key + ":" + stuff.value))
                    .map(KeyValue::value) // Get requestentity from KeyValue<queue name, request>
                    //.invoke((stuff)-> LOG.info("Got " + stuff))
                    .flatMap((request) // Now Multi<ItemWithContext<RequestEntity>>
                            -> {
                        LOG.info("Got request " + request.requestId + " with user: " + request.name + " and fnurgel: " + request.fnurgel);
                        // Create response
                        ResponseEntity response = new ResponseEntity(request.requestId, 0, "OK", "THIS IS A RESPONSE TO QUERY " + request.requestId);
                        // Push it back!
                        return responses.rpush(request.requestId, response).toMulti();
                    }
                    )
                    .subscribe().with(
                            input -> {
                                // Just drop these values... LOG.info("Stream input: " + input);
                            },
                            fail -> {
                                LOG.info("Got exception from stream: " + fail.getMessage());
                            }); 
        } catch (Exception ex) {
            LOG.info("Queue listener terminated by exception!");
        }
    }

}
