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
import javax.ws.rs.Consumes;

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

    ReactiveListCommands<String, String> lists = null; // Represents all lists in REDIS on port: 6379, same as in (Application.properties)
    ReactiveKeyCommands<String> keys = null;

    /**
     This method (although not pretty nor perfect) now works as intended. In THEORY, I should be able to write a backend
     that "reads" the localhost redis port (6379) and executes code based on the structure on what lies in the queue.

     Maybe having multiple "toRedis" methods in frontend that sends different crud messages (Post, get etc.) to different
     lists in redis and having different "readers" in backend is the best way. It seems most logical and I can see how
     that would function in practice
     */

    @ConsumeEvent("requestToRedis")
    public void handleToRedis(Message<RequestEntity> requestMsg) {
        // Add our lists and keys from our redis to here, so we can interact with them!
        if (lists == null) {
            lists = redis.list(String.class);
        }
        if (keys == null) {
            keys = redis.key();
        }

        LOG.info("Got message with body " + requestMsg.body() + " and replyAddress " + requestMsg.replyAddress());
        RequestEntity redisPayload = new RequestEntity();

        redisPayload.setName(requestMsg.body().getName());
        redisPayload.setAge(requestMsg.body().getAge());
        redisPayload.setPrefWeapon(requestMsg.body().getPrefWeapon());
        redisPayload.setSpecialAttack(requestMsg.body().getSpecialAttack());

        // Clear replyAddress from redis in case we have old stuff there
        keys.del(redisPayload.toString()).subscribe().with(response -> {
            System.out.println("keys.del ran");
        });

        LOG.info("Pushing payload to redis: " + redisPayload.getName());
        lists.rpush("toBackend", redisPayload.toString()).subscribe().with(response -> { // What exactly does .subscribe() do?
            System.out.println("Response = " + response);
        });

        // Interesting that this is returned
        System.out.println("redisPayload now returning to Resource...");
        requestMsg.reply(redisPayload);
    }




}