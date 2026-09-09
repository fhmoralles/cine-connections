package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieSearchResult(

        long id,

        String title,

        @JsonProperty("original_title")
        String originalTitle,

        @JsonProperty("release_date")
        String releaseDate,

        @JsonProperty("poster_path")
        String posterPath
) {
}
