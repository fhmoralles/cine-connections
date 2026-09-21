package com.cineconnections.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class CorsResourceTest {

    @Test
    void allowsConfiguredProductionOrigin() {
        given()
                .header("Origin", "https://cine-connections.com")
                .when().get("/health")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", "https://cine-connections.com");
    }

    @Test
    void rejectsUnknownOrigin() {
        given()
                .header("Origin", "https://evil.example")
                .when().get("/health")
                .then()
                .statusCode(403);
    }
}
