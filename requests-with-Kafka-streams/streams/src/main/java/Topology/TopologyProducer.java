package Topology;

import Entities.JoinedLoginEntity;
import Entities.LoginRequest;
import Entities.Response;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Duration;
import java.util.Objects;

@ApplicationScoped
public class TopologyProducer {
    // Kafka Topics
    private static final String LOGIN_REQUESTS_TOPIC = "login-requests";
    private static final String RESPONSES_TOPIC = "responses";
    private static final String VALIDATE_LOGIN_TOPIC = "validate-login-requests";

    /**
     This topology "reads" login-requests from frontend and joins them with results from backend by id.
     This way, we can easily tell what response belongs to which requests and weather or not it succeeded.
     */

    @Produces
    public Topology getLoginRequests() {
        // The "StreamsBuilder" object is used to create and return topology
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<LoginRequest> loginRequestSerde = new JsonbSerde<>(LoginRequest.class);
        JsonbSerde<Response> responseSerde = new JsonbSerde<>(Response.class);
        JsonbSerde<JoinedLoginEntity> validateLoginRequestsSerde = new JsonbSerde<>(JoinedLoginEntity.class);

        /*
        Turning streams into objects with injected serializers/deserializers can dramatically minimize the code
        one would have to write each time you'd want to interact with your streams programmatically
        */

        // Stream of login requests coming from a frontend
        KStream<String, LoginRequest> loginRequestStream = builder.stream(LOGIN_REQUESTS_TOPIC, Consumed.with(Serdes.String(), loginRequestSerde));

        // Stream of responses, these are the ones that comes back from backend
        KStream<String, Response> responseStream = builder.stream(RESPONSES_TOPIC, Consumed.with(Serdes.String(), responseSerde));

        // Isn't used, comment back in if needed (for some reason)
        // KStream<String, JoinedLoginEntity> validateLoginRequests = builder.stream(VALIDATE_LOGIN_TOPIC, Consumed.with(Serdes.String(), validateLoginRequestsSerde));


        /*
        THIS is a valueJoiner, it takes in one object, a second object and then the third object is returned,
        combining the two first values into a new JOINED value.
                  |1:st value| |2:nd value| |value to be returned|                                */
        ValueJoiner<Response, LoginRequest, JoinedLoginEntity> valueJoiner = (response, loginRequest) -> {
            // If-statement unnecessary since this value joiner is intended for left-joins
            if (Objects.equals(loginRequest.getRequestId(), response.getResponseId())){
                return new JoinedLoginEntity(loginRequest.getRequestId(), response.getResponseId(), loginRequest, response);
            }
            else return new JoinedLoginEntity(null, null, null, null);
        };


        // A left-join will attempt to join EACH record of the first/primary topic (in this case "responseStream")
        // with an equivalent record in the secondary topic ("loginRequestStream") that shares the same ID.
        // Should no data with said ID be available in the secondary topic, it will produce a join with a NULL value
                responseStream.leftJoin(
                                loginRequestStream,
                                valueJoiner,
                                JoinWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(2), Duration.ZERO),
                                StreamJoined.with(Serdes.String(), responseSerde, loginRequestSerde))
                                .to(
                                        VALIDATE_LOGIN_TOPIC,
                                        Produced.with(Serdes.String(), validateLoginRequestsSerde)
                                );

        return builder.build();
    }


}
