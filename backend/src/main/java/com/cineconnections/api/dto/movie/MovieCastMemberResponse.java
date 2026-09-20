package com.cineconnections.api.dto.movie;

import java.util.UUID;

public record MovieCastMemberResponse(

        UUID personId,

        Long tmdbId,

        String name,

        String characterName,

        String profilePath,

        Integer order
) {
}
