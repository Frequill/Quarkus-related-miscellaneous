package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.Order;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/redis/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from RESTEasy Reactive"));
    }
    
    
    @Test
    @Order(value = 30)
    public void testSetKeyEndpoint() {
        given()
          .when().body("1234").post("/redis/setkey/test1")
          .then()
             .statusCode(204);

        given()
          .when().get("/redis/getkey/test1")
          .then()
             .statusCode(200)
                .body(is("1234"));
    }

}