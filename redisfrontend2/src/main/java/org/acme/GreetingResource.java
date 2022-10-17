package org.acme;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import javax.inject.Inject;
import javax.ws.rs.*;
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

    /**
     * Curl path: curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"name":"masterLuke", "age":"25", "prefWeapon":"lightSaber", "specialAttack":"forcePush"}' "http://localhost:8080/redis/sendRequest"
     * <p>
     * curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"name":"testUser", "age":"ageBro", "prefWeapon":"sword", "specialAttack":"fireBreath"}' "http://localhost:8080/redis/sendRequest"
     */
    @POST
    @Path("/sendRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Message<RequestEntity>> sendQuery(RequestEntity request) {
        return eventbus.<RequestEntity>request("requestToRedis", request);
    }





}