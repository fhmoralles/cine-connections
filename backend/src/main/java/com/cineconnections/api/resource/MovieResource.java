package com.cineconnections.api.resource;

import com.cineconnections.api.dto.movie.MoviePageResponse;
import com.cineconnections.service.MoviePageService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MoviePageService moviePageService;

    @GET
    @Path("/{id}")
    public Response getMoviePage(
            @PathParam("id") UUID movieId
    ) {

        try {

            MoviePageResponse page =
                    moviePageService.getMoviePage(movieId);

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
