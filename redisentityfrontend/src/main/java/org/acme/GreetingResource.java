package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/redis")
public class GreetingResource {

    public static final Logger LOG = Logger.getLogger(GreetingResource.class);
    
    @Inject
    EventBus eventbus;
    
    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String, CommandEntity> lists = null;
    ReactiveListCommands<String,RequestEntity> requests = null;
    ReactiveListCommands<String, ResponseEntity> responses = null;
    ReactiveKeyCommands<String> keys = null;
    
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
    
    
    @Path("/sendquery/{arg}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> sendquery(@PathParam("arg") String arg) {
        
        // Send request via eventbus to bean responsible for Redis
        // Just ignore this line for now
        eventbus.send("backendinit", "run");
        
        return eventbus.<String>request("toRedis", arg).onItem().transform(Message::body);
        
    }
    
    AtomicInteger idcounter = new AtomicInteger(0);
    
    @Path("/sendredis/{arg}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommandEntity> sendredis(@PathParam("arg") String arg) {
        
        // Send request via eventbus to bean responsible for Redis
        // Just ignore this line for now
        eventbus.send("backendinit", "run");
        
        //return eventbus.<String>request("toRedis", arg).onItem().transform(Message::body);
        
        String requestId = "request_" + idcounter.getAndIncrement();
        
        if (lists == null) {
            lists = redis.list(CommandEntity.class);
        }
        if (keys == null) {
            keys = redis.key();
        }
        LOG.info("Got message with body " + arg + " and replyAddress" + requestId);
        List<PairEntity<String,String>> args = new LinkedList<>();
        args.add(new PairEntity("user", "jonas"));
        args.add(new PairEntity("password", "fnurgelburgel"));
        final CommandEntity redisPayload = new CommandEntity(requestId, "login", args);
        //final String redisPayload = requestId; // For now just send reply address
        // Clear replyAddress from redis in case we have old stuff there
        keys.del(requestId);

        LOG.info("Pushing payload to redis: " + redisPayload);
        return lists.rpush("tobackend", redisPayload).flatMap(response -> {
            LOG.info("Got response from redis: " + response + ", starting response listener");
            // After sending event, listen for response and send back as Uni.
            //return lists.blpop(Duration.ofSeconds(10), redisPayload).onItem().ifNull().continueWith(new KeyValue<String,String>(null, "NO DATA FROM REDIS")).map(KeyValue::value);
            // Geewiz Golly!
            // NOTE: data flows to the right in expression.
            return lists.blpop(Duration.ofSeconds(10), requestId)
                    .onItem().ifNotNull()
                    .transform(KeyValue::value).invoke((ce) -> LOG.info("Response id: " + ce.responseQueue + ", response: " + ce.command))
                    .onItem().ifNull().continueWith(new CommandEntity(requestId, "FAILURE", null));
        });
    }
    
    
    
    
    @Path("/sendrequest")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<ResponseEntity> sendredis(RequestEntity arg) {
        
        // Send request via eventbus to bean responsible for Redis
        // Just ignore this line for now
        eventbus.send("backendinit", "run");
        
        //return eventbus.<String>request("toRedis", arg).onItem().transform(Message::body);
        
        String requestId = "request_" + idcounter.getAndIncrement();
        
        if (requests == null) {
            requests = redis.list(RequestEntity.class);
        }
        if (responses == null) {
            responses = redis.list(ResponseEntity.class);
        }
        if (keys == null) {
            keys = redis.key();
        }
        
        LOG.info("Received request: id="+arg.requestId + ", name="+arg.name + ", fnurgel=" + arg.fnurgel);
        LOG.info("Overwriting requestId with: " + requestId);
        arg.requestId=requestId;
        //final String redisPayload = requestId; // For now just send reply address
        // Clear replyAddress from redis in case we have old stuff there
        keys.del(requestId);

        LOG.info("Pushing payload to redis");
        return requests.rpush("requests", arg).flatMap(response -> {
            LOG.info("Got response from redis: " + response + ", starting response listener");
            // After sending event, listen for response and send back as Uni.
            //return lists.blpop(Duration.ofSeconds(10), redisPayload).onItem().ifNull().continueWith(new KeyValue<String,String>(null, "NO DATA FROM REDIS")).map(KeyValue::value);
            // Geewiz Golly!
            // NOTE: data flows to the right in expression.
            return responses.blpop(Duration.ofSeconds(10), requestId)
                    .onItem().ifNotNull()
                    .transform(KeyValue::value).invoke((ce) -> LOG.info("Response id: " + ce.requestId + ", response: " + ce.response))
                    .onItem().ifNull().continueWith(new ResponseEntity());
        });
    }

}