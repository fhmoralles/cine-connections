package com.cineconnections.api.dto.person;

import com.cineconnections.api.dto.movie.MovieSummaryResponse;

import java.util.List;

public record PersonPageResponse(

        PersonProfileResponse person,

        List<MovieSummaryResponse> movies,

        List<CoActorResponse> frequentCoActors,

        long movieCount,

        long coActorCount
) {
}
