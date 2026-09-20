package com.cineconnections.api.dto.movie;

import java.util.UUID;

public record MovieSummaryResponse(

        UUID id,

        Long tmdbId,

        String title,

        Integer releaseYear,

        String posterPath,

        String backdropPath
) {
}
