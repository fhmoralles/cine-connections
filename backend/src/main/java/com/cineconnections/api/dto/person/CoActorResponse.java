package com.cineconnections.api.dto.person;

import java.util.UUID;

public record CoActorResponse(

        UUID personId,

        Long tmdbId,

        String name,

        String profilePath,

        long sharedMovieCount
) {
}
