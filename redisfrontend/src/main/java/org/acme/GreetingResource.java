package org.acme;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/redis")
public class GreetingResource {

    @Inject
    EventBus eventbus;
    
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
        eventbus.send("backendinit", "run");
        
        return eventbus.<String>request("toRedis", arg).onItem().transform(Message::body);
        
    }
}