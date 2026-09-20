package com.cineconnections.api.resource;

import com.cineconnections.api.dto.person.PersonPageResponse;
import com.cineconnections.api.dto.person.PersonSearchResponse;
import com.cineconnections.api.dto.person.PersonSearchResult;
import com.cineconnections.service.PersonPageService;
import com.cineconnections.service.PersonSearchService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    PersonSearchService personSearchService;

    @Inject
    PersonPageService personPageService;

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

    @GET
    @Path("/{id}")
    public Response getPersonPage(
            @PathParam("id") UUID personId
    ) {

        try {

            PersonPageResponse page =
                    personPageService.getPersonPage(personId);

            return Response.ok(page).build();

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

