package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieDetails(

        long id,

        String title,

        @JsonProperty("original_title")
        String originalTitle,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("backdrop_path")
        String backdropPath,

        String overview,

        @JsonProperty("release_date")
        String releaseDate
) {
}
