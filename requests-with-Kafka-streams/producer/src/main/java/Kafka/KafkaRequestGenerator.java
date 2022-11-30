package Kafka;

import RequestEntities.LoginRequest;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class KafkaRequestGenerator {

    private Random random = new Random();

    @Inject
    Logger LOG;

    /**
     Counter that is incremented by one each time it is retrieved with the "makeId" method. This is how I
     generate an ID for each request being sent to backend.
     */
    public static int idCounter = 0;
    public int makeId() {
        idCounter++;
        return idCounter;
    }

    /**
     Placeholder list contains a bunch of fake login-requests just so that we can have some diversity and not
     get a generic request without any 'flavor' every single time one is generated.

     (Note that the ID is 0 on every placeholder request just so that I can give each request a proper ID before it is
     sent out so that the id itself doesn't have to be hardcoded)
     */
    private final List<LoginRequest> loginRequestsList = List.of(
            new LoginRequest("0", "360noScope!", "password"),
            new LoginRequest("0", "Mange Uggla", "barKnugen"),
            new LoginRequest("0","Michael Bay", "IllegalC4"),
            new LoginRequest("0","JediLuke", "Sky-walking"),
            new LoginRequest("0","AliensAreReal!", "Mr.Cruise"),
            new LoginRequest("0","Farsan", "hemligtLösenord"),
            new LoginRequest("0","Arne-Barnarne", "Trädkoja"),
            new LoginRequest("0","Nisse-Tandborste", "hundStund")
    );


    /**
     Spits out a randomly generated login-request to the "requests" table every 4 seconds,
     uses "loginRequestsList" as a template-list
     */
    @Outgoing("login-requests")
    public Multi<Record<String, LoginRequest>> generateRequests() {
        // TODO: Find out how to make the "ticks" be randomized.
        //  When I tried with a "Random" the number always became static
        return Multi.createFrom().ticks().every(Duration.ofSeconds(4))
                .onOverflow().drop()
                .map(tick -> {
                    int id = makeId();
                    LoginRequest loginRequest = loginRequestsList.get(random.nextInt(loginRequestsList.size()));
                    LOG.info("RequestID: " + id + "  Username: " + loginRequest.getUsername());

                    return Record.of(String.valueOf(id), new LoginRequest(String.valueOf(id), loginRequest.getUsername(), loginRequest.getPassword()));
                });
    }



}