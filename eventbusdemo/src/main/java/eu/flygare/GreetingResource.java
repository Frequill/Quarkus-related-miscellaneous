package eu.flygare;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.mutiny.core.MultiMap;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

@Path("/demo")
public class GreetingResource {

    @Inject
    EventBus eventbus;

    //@RestClient
    //MyRestClient restclient;
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Path("/logbus")
    @Produces(MediaType.TEXT_PLAIN)
    public String doLogData(@QueryParam("data") @DefaultValue("Dummydata") String data) {
        eventbus.publish("EB_mylogger", data);
        return "Sent data \"" + data + "\" to backend logger via eventbus";
    }

    @GET
    @Path("/2backend")
    @Produces(MediaType.TEXT_PLAIN)
    public String doCallBackend(@QueryParam("data") String data) {
        //String response = restclient.doGetCall(String data);
        eventbus.publish("EB_mylogger", "Data for backend: " + data);
        Message<String> result = eventbus.requestAndAwait("EB_backend", data);
        return result.body();
    }

    ///---------------------AND THEN PAIN HAPPENS------------------------------
    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    public String doLogin(@QueryParam("data") String data) {
        RequestEntity request = new RequestEntity();
        request.data = data;
        request.requestType = "login";
        Message<ResponseEntity> responseMsg = eventbus.requestAndAwait("EB_dispatch", request);
        ResponseEntity resp = responseMsg.body();
        eventbus.publish("EB_mylogger", "doLogin got status " + resp.resultStatus);
        return resp.data;
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public String doLogout(@QueryParam("data") String data) {
        RequestEntity request = new RequestEntity();
        request.data = data;
        request.requestType = "logout";
        Message<ResponseEntity> responseMsg = eventbus.requestAndAwait("EB_dispatch", request);
        ResponseEntity resp = responseMsg.body();
        eventbus.publish("EB_mylogger", "doLogout got status " + resp.resultStatus);
        return resp.data;
    }

    @GET
    @Path("/nodata")
    @Produces(MediaType.TEXT_PLAIN)
    public String doNodata() {
        Message<String> result = eventbus.requestAndAwait("nodata", null);
        return result.body();
    }
    
    @GET
    @Path("/noreply")
    @Produces(MediaType.TEXT_PLAIN)
    public String doNoreply() {
        // Leker lite med headers här, men de är vert.x-specifika, så vi kan inte ta det med oss till
        // Redis/Kafka misstänker jag.
        // Om eventbussen har metadata så kan man där ange typ på event istället för att packa ihop en
        // metaEntity med typ + data.
        eventbus.send("nodata", null, new DeliveryOptions().addHeader("NORESPONSE", "true").addHeader("SENDER", "jonasf"));
        return "NOREPLY";
    }
    
}
