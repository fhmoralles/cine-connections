package com.cineconnections.api.dto.movie;

import java.util.List;

public record MoviePageResponse(

        MovieSummaryResponse movie,

        List<MovieCastMemberResponse> cast,

        List<MovieSummaryResponse> similarMovies,

        List<MovieSummaryResponse> otherMoviesWithCast
) {
}
