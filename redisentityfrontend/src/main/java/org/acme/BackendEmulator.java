/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
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
public class BackendEmulator {

    public static final Logger LOG = Logger.getLogger(BackendEmulator.class);

    @Inject
    ReactiveRedisDataSource redis;

    Boolean notstarted = true;

    @ConsumeEvent("backendinit")
    public void StartBackendEmulator(String data) {
        LOG.info("Backend got " + data + " notstarted=" + notstarted);
        if (notstarted) {
            notstarted = false;
            Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> redis.list(CommandEntity.class).blpop(Duration.ofSeconds(10), "tobackend")).indefinitely()
                    .invoke((stuff) -> LOG.info("Got " + stuff.key + ":" + stuff.value))
                    .map(KeyValue::value)
                    //.invoke((stuff)-> LOG.info("Got " + stuff))
                    .flatMap((replylist)
                            -> {
                        LOG.info("Got request " + replylist.responseQueue + " with command " + replylist.command);
                        CommandEntity response = new CommandEntity(replylist.responseQueue, "THIS IS A RESPONSE TO QUERY " + replylist.responseQueue, null);
                        return redis.list(CommandEntity.class).rpush(replylist.responseQueue, response).toMulti();
                    }
                    ).subscribe().with(foo -> {/* LOG IF NEEDED. It should be 1 (the Atomic in the start) */
                    });
            Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> redis.list(RequestEntity.class).blpop(Duration.ofSeconds(10), "requests")).indefinitely()
                    .invoke((stuff) -> LOG.info("Got " + stuff.key + ":" + stuff.value))
                    .map(KeyValue::value)
                    //.invoke((stuff)-> LOG.info("Got " + stuff))
                    .flatMap((replylist)
                            -> {
                        LOG.info("Got request " + replylist.requestId + " with user: " + replylist.name + " and fnurgel: " + replylist.fnurgel);
                        ResponseEntity response = new ResponseEntity(replylist.requestId, 0, "OK", "THIS IS A RESPONSE TO QUERY " + replylist.requestId);
                        return redis.list(ResponseEntity.class).rpush(replylist.requestId, response).toMulti();
                    }
                    ).subscribe().with(foo -> {/* LOG IF NEEDED. It should be 1 (the Atomic in the start) */
                    });
        }
    }

}
