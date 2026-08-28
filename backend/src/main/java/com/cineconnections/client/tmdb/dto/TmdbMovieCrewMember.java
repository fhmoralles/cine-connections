package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieCrewMember(

        long id,

        String name,

        String department,

        String job,

        @JsonProperty("profile_path")
        String profilePath

) {
}
