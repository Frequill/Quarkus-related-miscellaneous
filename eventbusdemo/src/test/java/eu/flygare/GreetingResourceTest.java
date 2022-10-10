package eu.flygare;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/demo/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from RESTEasy Reactive"));
    }

    @Test
    public void testEvenbus1Endpoint() {
        given()
          .when().get("/demo/logbus")
          .then()
             .statusCode(200)
             .body(is("Sent data \"Dummydata\" to backend logger via eventbus"));
    }
    
}