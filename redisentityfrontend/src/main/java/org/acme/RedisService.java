/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

/**
 *
 * @author flax
 */
@ApplicationScoped
public class RedisService {

    Logger LOG = Logger.getLogger(RedisService.class);

    ReactiveListCommands<String, RequestEntity> requests;
    ReactiveListCommands<String, ResponseEntity> responses;
    ReactiveKeyCommands<String> keys;

    AtomicInteger idcounter = new AtomicInteger(0);

    public RedisService(ReactiveRedisDataSource ds) {

        requests = ds.list(RequestEntity.class);
        responses = ds.list(ResponseEntity.class);
        keys = ds.key();
    }

    public Uni<ResponseEntity> sendRequest(RequestEntity arg) {
        String requestId = arg.requestId;
        if (requestId == null) {
            requestId = "generated_id_" + idcounter.getAndIncrement();
            arg.requestId = requestId;
        }
        keys.del(requestId); // Make sure no garbage interferes between runs

        return requests.rpush("requests", arg).flatMap(response -> {
            return fetchResponse(arg.requestId, 10L);
        });

    }

    private Uni<ResponseEntity> fetchResponse(String requestId, Long timeoutInSeconds) {
        try {
            return responses.blpop(Duration.ofSeconds(timeoutInSeconds), requestId)
                    .onItem().ifNotNull()
                    .transform(KeyValue::value) // Extract ResponseEntity from Redis response
                    // .invoke((ce) -> LOG.info("Response id: " + ce.requestId + ", response: " + ce.response)) // Log
                    // Handle null response
                    .onItem().ifNull().continueWith(new ResponseEntity(requestId, -1, "ERROR", null))
                    .onFailure().recoverWithItem(new ResponseEntity(requestId, -3, "ERRORHANDLER", null));
        } catch (Exception ex) {
            LOG.info("Frontend listener terminated by exception");
            return Uni.createFrom().item(new ResponseEntity(requestId, -2, "TERMINATED", null));
        }
    }
}
