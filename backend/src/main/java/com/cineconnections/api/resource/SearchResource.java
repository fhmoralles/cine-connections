package com.cineconnections.api.resource;

import com.cineconnections.api.dto.search.SearchResponse;
import com.cineconnections.service.SearchService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    @Inject
    SearchService searchService;

    @GET
    public SearchResponse search(
            @QueryParam("query") String query
    ) {

        return searchService.search(query);
    }
}
