import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

/**
  A bean consuming data from the "quote-requests" Kafka topic (mapped to "requests" channel) and giving out a random quote.
  The result is pushed to the "quotes" Kafka topic.
 */
@ApplicationScoped
public class QuotesProcessor {

    private Random random = new Random();


    @Incoming("requests") // Method will CONSUME items from the "requests" channel
    @Outgoing("quotes")   // Objects returned are sent to the "quotes" channel
    @Blocking // Can't be run on the caller thread... because it's blocking
    public Quote process(String quoteRequest) throws InterruptedException {
        // Simulating a very hard task that freezes CPU for a second. WOW, THAT'S A LOT OF WORK!
        Thread.sleep(2000);
        return new Quote(quoteRequest, random.nextInt(100));
    }



}