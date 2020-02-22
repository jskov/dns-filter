package accepttest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test that quarkus end-to-end testing actually works.
 */
@Tag("accept")
@QuarkusTest
public class RuntimePingTest {

    @Test
    public void testPingEndpoint() {
        given()
          .when().get("/ping")
          .then()
             .statusCode(200)
             .body(is("pong"));
    }
}