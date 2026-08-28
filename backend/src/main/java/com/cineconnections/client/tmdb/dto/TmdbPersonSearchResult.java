package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbPersonSearchResult(

        long id,

        String name,

        double popularity,

        @JsonProperty("profile_path")
        String profilePath

) {
}
