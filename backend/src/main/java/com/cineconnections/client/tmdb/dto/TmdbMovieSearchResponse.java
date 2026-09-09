package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieSearchResponse(
        int page,
        List<TmdbMovieSearchResult> results
) {
}
