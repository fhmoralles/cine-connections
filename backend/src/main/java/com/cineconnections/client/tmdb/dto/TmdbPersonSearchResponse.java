package com.cineconnections.client.tmdb.dto;

import java.util.List;

public record TmdbPersonSearchResponse(
        int page,
        List<TmdbPersonSearchResult> results,
        int total_pages,
        int total_results
) {
}
