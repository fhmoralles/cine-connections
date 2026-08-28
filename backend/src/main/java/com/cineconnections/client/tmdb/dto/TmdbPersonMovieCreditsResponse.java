package com.cineconnections.client.tmdb.dto;

import java.util.List;

public record TmdbPersonMovieCreditsResponse(

        List<TmdbCastCredit> cast,

        List<TmdbCrewCredit> crew

) {
}
