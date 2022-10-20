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
     Call with: curl -H "Content-Type: application/json" -H "Accepts: application/json" -X POST -d '{"name":"testUser", "specialAttack":"fireBreath"}' "http://localhost:8080/redis/sendRequest"

     This method creates a RequestEntity per user input in POST call. It then places said entity in a redis list called
     "requests". It then waits for backend to respond by placing a respons in a new separate list with the same name
     as the original request's ID. If found, this method will return the respons from redis that was sent from backend.
     */

    @Path("/sendRequest")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ResponseEntity> sendRequest(RequestEntity arg) {

        // Make and set the request ID for the entity in question
        String requestId = arg.getName() + "_request" + idCounter.getAndIncrement();
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


        return requests.rpush("requests", arg).flatMap(response -> {

            //              "response" here should be a 0 or a 1 depending on success or failure
            System.out.println("Redis responded with: " + response + " will now listen for response...");

            // Waits for 10 SECONDS to see if a list with the same name as the requestId pops up in redis from backend
            return responses.blpop(Duration.ofSeconds(10), requestId)
                    .onItem().ifNotNull()
                    .transform(KeyValue::value).invoke((ce) -> System.out.println("Response id: " + ce.requestId
                            + ", response = " + ce.response))
                    .onItem().ifNull().continueWith(new ResponseEntity());
        });

        //return requests.rpush("requests", arg);
    }


}