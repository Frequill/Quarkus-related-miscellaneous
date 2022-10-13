/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.vertx.ConsumeEvent;
import java.time.Duration;
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
    
    @ConsumeEvent("backendinit")
    public void StartBackendEmulator(String data) {
        LOG.info("Backend started");
        redis.list(String.class).brpop(Duration.ofSeconds(20), "tobackend").subscribe().with(result -> {
            // For now result is the queue we reply to...
            LOG.info("Read data from frontend: " + result.value());
            if (result.value != null && !result.value.endsWith("5")) {
            redis.list(String.class).rpush(result.value, "\"Backend handled data: " + result.value + "\"").subscribe().with(
                    go -> {
                        LOG.info("Pushed data to list " + result.value);
                    },
                    fail -> {
                        LOG.info("Failed to push data");
                    });} else {
                LOG.info("Dropped data for reply with id " + result.value);
            }
        });
    }
    
}
