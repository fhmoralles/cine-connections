package com.cineconnections.api.dto.person;

public record PersonSearchResult(
        String id,
        Long tmdbId,
        String name,
        String profilePath,
        boolean imported
) {
}
