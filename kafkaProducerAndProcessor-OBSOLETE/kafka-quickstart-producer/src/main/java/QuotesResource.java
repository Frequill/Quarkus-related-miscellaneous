import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 TIP FOR THE FUTURE: IF/WHEN you have MULTIPLE "connectors"/channels/@Channels,
 you need to indicate which connector you want to use in the app configuration!
 */

@Path("/quotes")
public class QuotesResource {

    // Inject a Reactive Messaging Emitter to send messages to the quote-requests channel.
    @Channel("quote-requests") // Quarkus uses the channel name as a topic name, this app will write into the "quote-requests" topic
    Emitter<String> quoteRequestEmitter;


    // No need to configure anything, as Quarkus will automatically associate the quotes channel to the quotes Kafka topic
    @Channel("quotes") // Injects the "quotes" channel
    Multi<Quote> quotes;





    // ************************************************* Methods *******************************************************

    /**
     Endpoint to generate a new quote request id and send it to "quote-requests" Kafka topic using the emitter
     */
    @POST
    @Path("/request")
    @Consumes(MediaType.TEXT_PLAIN) // Added this myself, if something bugs kill it off permanently! Mu haha
    @Produces(MediaType.TEXT_PLAIN)
    public String createRequest() {
        UUID uuid = UUID.randomUUID();
        quoteRequestEmitter.send(uuid.toString()); // Generate a random UUID and send it to the Kafka topic using the emitter
        return uuid.toString(); // Return the *SAME* UUID to the client.
    }

    /**
     Endpoint retrieving the "quotes" Kafka topic and sending the items to a server sent event.

     Might consider renaming this method to like "getQuotes" or something
     */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) // Content is sent using Server Sent Events
    public Multi<Quote> stream() {
        return quotes;
    }



}