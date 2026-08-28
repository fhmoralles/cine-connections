package com.cineconnections.client.tmdb;

import com.cineconnections.client.tmdb.dto.TmdbPersonDetails;
import com.cineconnections.client.tmdb.dto.TmdbPersonMovieCreditsResponse;
import com.cineconnections.client.tmdb.dto.TmdbPersonSearchResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "tmdb-api")
@RegisterProvider(TmdbAuthorizationFilter.class)
public interface TmdbClient {

    @GET
    @Path("/search/person")
    TmdbPersonSearchResponse searchPerson(
            @QueryParam("query") String query,
            @QueryParam("language") String language,
            @QueryParam("page") int page,
            @QueryParam("include_adult") boolean includeAdult
    );

    @GET
    @Path("/person/{personId}")
    TmdbPersonDetails getPerson(
            @PathParam("personId") long personId,
            @QueryParam("language") String language
    );

    @GET
    @Path("/person/{personId}/movie_credits")
    TmdbPersonMovieCreditsResponse getMovieCredits(
            @PathParam("personId") long personId,
            @QueryParam("language") String language
    );
}
