/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;



/**
 *
 * @author flax
 */
@ApplicationScoped
public class ReactiveRedisClient {
    
    public static final Logger LOG = Logger.getLogger(ReactiveRedisClient.class);
    
    private ReactiveKeyCommands<String> keyCommands; 
    private ReactiveStringCommands<String,Integer> stringCommands;
    private ReactiveListCommands<String, String> listCommands;

    public ReactiveRedisClient(ReactiveRedisDataSource reactive) { 
        keyCommands = reactive.key(); 
        stringCommands = reactive.string(Integer.class);
        listCommands = reactive.list(String.class);

    }
    
    public Uni<Void> setKey(String key, Integer value) {
        return stringCommands.set(key, value);
    }
    
    public Uni<Integer> getKey(String key) {
        return stringCommands.get(key);
    }
    
    public Uni<Long> addToEventQueue(String list, String item) {
        LOG.info("rpush for list " + list + ", item: " + item);
        return listCommands.rpush(list, item);
    }
    
    public Uni<KeyValue<String,String>> getFromEventQueue(String list) {
        LOG.info("Got bpop call for list " + list);
        return listCommands.blpop(Duration.ofSeconds(10), list);
    }
    
    
}
