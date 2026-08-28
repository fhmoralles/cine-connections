package com.cineconnections.client.tmdb.dto;

import java.util.List;

public record TmdbMovieCreditsResponse(

        long id,

        List<TmdbMovieCastMember> cast,

        List<TmdbMovieCrewMember> crew

) {
}
