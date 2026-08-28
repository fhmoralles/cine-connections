package com.cineconnections.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    public HealthResponse check() {
        return new HealthResponse(
                "UP",
                "Cine Connections is running"
        );
    }

    public record HealthResponse(
            String status,
            String message
    ) {
    }
}
