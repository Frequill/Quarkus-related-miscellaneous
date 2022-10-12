/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author flax
 */
@ApplicationScoped
public class VertxRedisClient {

    @Inject
    Vertx vertx;


    private RedisAPI redisapi;

    public void connectToRedis() {
       Redis.createClient(
                vertx,
                // The client handles REDIS URLs. The select database as per spec is the
                // numerical path of the URL and the password is the password field of
                // the URL authority
                "redis://localhost:6379")
                .connect()
                .onSuccess(conn -> {

                    System.out.println("Connected!");
                    redisapi = RedisAPI.api(conn);
                    // use the connection
                    redisapi.set(List.of("kalle", "kula")).onSuccess(succ -> {
                        System.out.println("Key kalle set!");
                    });
                }).result();
        
    }  

}
