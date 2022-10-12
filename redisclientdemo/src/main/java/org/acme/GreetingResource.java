package org.acme;

import io.quarkus.redis.datasource.list.KeyValue;
import io.smallrye.mutiny.Uni;
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
    ReactiveRedisClient redisclient;
    
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
    
    
    @POST
    @Path("/setkey/{key}")
    public Uni<Void> setKey(@PathParam("key") String key, String value) {
        return redisclient.setKey(key, Integer.valueOf(value));
    }

    @GET
    @Path("/getkey/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> getKey(@PathParam("key") String key) {
        return redisclient.<Integer>getKey(key).onItem().transform(String::valueOf);
    }
    
    @POST
    @Path("/push/{key}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Uni<Long> pushToEventQueue(@PathParam("key") String key, String value) {
        LOG.info("Got call to rpush on list " + key + ", item: " + value);
        return redisclient.addToEventQueue(key, value);
    }
    
    @GET
    @Path("/bpop/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> popFromEventQueue(@PathParam("key") String key) {
        LOG.info("Got call to bpop for list " + key);
        return redisclient.<String>getFromEventQueue(key).onItem().transform(KeyValue::value);
    }
    
    @GET
    @Path("/bpopkv/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<KeyValue<String,String>> popFromEventQueueKV(@PathParam("key") String key) {
        LOG.info("Got call to bpop for list " + key);
        return redisclient.<String>getFromEventQueue(key);
    }

}