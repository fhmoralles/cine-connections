package com.cineconnections.api.resource;

import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.repository.MovieRepository;
import com.cineconnections.service.MovieExpansionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieExpansionResource {

    @Inject
    MovieRepository movieRepository;

    @Inject
    MovieExpansionService movieExpansionService;

    @POST
    @Path("/{id}/expand")
    public Response expandMovie(
            @PathParam("id") UUID id
    ) {

        Movie movie = movieRepository
                .findByIdOptional(id)
                .orElse(null);

        if (movie == null) {
            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        var result =
                movieExpansionService.expandMovie(movie);

        return Response.ok(result).build();
    }
}
