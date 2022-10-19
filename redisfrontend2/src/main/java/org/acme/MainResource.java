package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/redis")
public class MainResource {

    /* TODO: Ignore and continue without eventbus! Eventbus can be used IF we need to send something between frontend
        and backend, but our main goal is to use REDIS as the sole middle-man instead.
            Re-write Jonas's code to the point where it does what you want and you UNDERSTAND IT. Afterwards you make
                your backend "read" redis. Got it? Good.

     */

    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String,RequestEntity> requests = null;

    ReactiveListCommands<String, ResponseEntity> responses = null;

    ReactiveKeyCommands<String> keys = null;

    AtomicInteger idCounter = new AtomicInteger(0);


    /**
     Call attempt: curl -H "Content-Type: application/json" -H "Accepts: application/json" -X POST -d '{"name":"testUser", "specialAttack":"fireBreath"}' "http://localhost:8080/redis/sendRequest"
     */

    @Path("/sendRequest")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Long> sendRequest(RequestEntity arg) {

        // Make and set the request ID for the entity in question
        String requestId = arg.getName() + "_request" + idCounter;
        arg.setRequestId(requestId);

        // Make sure requests, responses and keys are retrieved from redis if they are null/has not yet been initialized
        if (requests == null) {
            requests = redis.list(RequestEntity.class);
        }
        if (responses == null) {
            responses = redis.list(ResponseEntity.class);
        }
        if (keys == null) {
            keys = redis.key();
        }

        System.out.println("Received request: id=" + arg.getRequestId() + ", name=" + arg.getName() + ", specialAttack=" + arg.getSpecialAttack());
        System.out.println("Now pushing payload to REDIS...");

        /*

            THIS CODE is meant for when backend can "READ" your redis request, this will listen for a response

        return requests.rpush("requests", arg).flatMap(response -> {
            System.out.println("Redis responded with: " + response + " will now listen for response...");

            // Attempts to listen for a response from a backend of some sort,
            // but since no such backend exists will produce expected error for now
            return responses.blpop(Duration.ofSeconds(10), requestId)
                    .onItem().ifNotNull()
                    .transform(KeyValue::value).invoke((ce) -> System.out.println("Response id: " + ce.requestId
                            + ", response = " + ce.response))
                    .onItem().ifNull().continueWith(new ResponseEntity());
        }); */

        return requests.rpush("requests", arg);

        /*
       requests.rpush("requests", arg).onItem().invoke((x) -> {
            System.out.println("rpush: " + x);
        });

        return Uni.createFrom().item(new ResponseEntity());
        */
    }


}