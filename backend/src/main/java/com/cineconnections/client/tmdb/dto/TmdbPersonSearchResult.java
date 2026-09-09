package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbPersonSearchResult(

        long id,

        String name,

        double popularity,

        @JsonProperty("profile_path")
        String profilePath

) {
}
