package com.cineconnections.api.dto.search;

public record SearchResult(
        SearchResultType type,
        String id,
        Long tmdbId,
        String name,
        String secondaryInfo,
        String imagePath,
        boolean imported
) {
}
