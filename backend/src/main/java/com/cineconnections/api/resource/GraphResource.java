package com.cineconnections.api.resource;

import com.cineconnections.api.dto.graph.GraphResponse;
import com.cineconnections.service.GraphService;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/graph")
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    GraphService graphService;

    @GET
    @Path("/person/{id}")
    public Response getPersonGraph(
            @PathParam("id") UUID personId,
            @QueryParam("depth") @DefaultValue("2") int depth
    ) {

        try {

            GraphResponse graph =
                    graphService.getPersonGraph(
                            personId,
                            depth
                    );

            return Response.ok(graph).build();

        } catch (IllegalArgumentException exception) {

            return Response.status(
                    Response.Status.NOT_FOUND
            ).entity(
                    new ErrorResponse(
                            exception.getMessage()
                    )
            ).build();
        }
    }

    public record ErrorResponse(
            String message
    ) {
    }
}
