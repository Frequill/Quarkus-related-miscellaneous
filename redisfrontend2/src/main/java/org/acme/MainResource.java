package org.acme;

import io.quarkus.redis.datasource.list.KeyValue;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/redis")
public class MainResource {

    /* TODO: Ignore and continue without eventbus! Eventbus can be used IF we need to send something between frontend
        and backend, but our main goal is to use REDIS as the sole middle-man instead.
            Re-write Jonas's code to the point where it does what you want and you UNDERSTAND IT. Afterwards you make
                your backend "read" redis. Got it? Good.

     */



    @Path("/sendrequest")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<ResponseEntity> sendredis(RequestEntity arg) {

        // Make me return something...

    }






}