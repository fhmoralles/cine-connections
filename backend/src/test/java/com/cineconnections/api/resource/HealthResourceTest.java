package com.cineconnections.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HealthResourceTest {

    @Test
    void healthEndpointReturnsUp() {
        given()
                .when().get("/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"))
                .body("message", is("Cine Connections is running"));
    }
}
