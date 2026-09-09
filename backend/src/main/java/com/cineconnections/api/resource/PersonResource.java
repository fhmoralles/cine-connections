package com.cineconnections.api.resource;

import com.cineconnections.api.dto.person.PersonSearchResponse;
import com.cineconnections.api.dto.person.PersonSearchResult;
import com.cineconnections.service.PersonSearchService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    PersonSearchService personSearchService;

    @GET
    @Path("/search")
    public Response search(
            @QueryParam("query") String query
    ) {

        if (query == null || query.isBlank()) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(
                            "Search query is required"
                    ))
                    .build();
        }

        var result =
                personSearchService.search(query.trim());

        return Response.ok(
                new PersonSearchResponse(
                        result.results().stream()
                                .map(person ->
                                        new PersonSearchResult(
                                                person.id(),
                                                person.tmdbId(),
                                                person.name(),
                                                person.profilePath(),
                                                person.imported()
                                        )
                                )
                                .toList(),
                        result.imported()
                )
        ).build();
    }

    public record ErrorResponse(
            String message
    ) {
    }
}
