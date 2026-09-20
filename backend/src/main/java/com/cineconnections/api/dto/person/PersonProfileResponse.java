package com.cineconnections.api.dto.person;

import java.util.UUID;

public record PersonProfileResponse(

        UUID id,

        Long tmdbId,

        String name,

        String profilePath
) {
}
