package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieCastMember(

        long id,

        String name,

        String character,

        @JsonProperty("profile_path")
        String profilePath,

        Integer order

) {
}
