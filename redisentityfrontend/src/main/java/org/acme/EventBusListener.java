/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

/**
 *
 * @author flax
 */
@ApplicationScoped
public class EventBusListener {

    public static final Logger LOG = Logger.getLogger(EventBusListener.class);

    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String, String> lists = null;
    ReactiveKeyCommands<String> keys = null;

    @ConsumeEvent("toRedis")
    public void handleToRedis(Message<String> msg) {
        if (lists == null) {
            lists = redis.list(String.class);
        }
        if (keys == null) {
            keys = redis.key();
        }
        LOG.info("Got message with body " + msg.body() + " and replyAddress" + msg.replyAddress());
        final String redisPayload = msg.replyAddress(); // For now just send reply address
        // Clear replyAddress from redis in case we have old stuff there
        keys.del(redisPayload);

        LOG.info("Pushing payload to redis: " + redisPayload);
        lists.rpush("tobackend", redisPayload).subscribe().with(response -> {
            LOG.info("Got response from redis: " + response + ", starting response listener");
            lists.blpop(Duration.ofSeconds(10), redisPayload).subscribe().with(readresp -> {
                // For now cheat, should read msg replyAddress from readresp
                if (readresp != null) {
                    LOG.info("Read from list " + readresp.key + ", value " + readresp.value + " replying and removing message");
                    msg.reply(readresp.value);
                } else {
                    LOG.info("blpop from list " + redisPayload + " timed out!");
                    msg.reply("FAILED TO GET RESULT FROM REDIS!!!");
                }
            }, fail -> {
                LOG.info("blpop from list " + redisPayload + " failed!");
                msg.reply("FAILED TO GET RESULT FROM REDIS!!!");
            });
        });

    }
}
