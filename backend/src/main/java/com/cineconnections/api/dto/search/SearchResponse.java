package com.cineconnections.api.dto.search;

import java.util.List;

public record SearchResponse(
        List<SearchResult> results
) {
}
