package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbCastCredit(

        long id,

        String title,

        @JsonProperty("release_date")
        LocalDate releaseDate,

        @JsonProperty("poster_path")
        String posterPath,

        String character,

        @JsonProperty("genre_ids")
        List<Integer> genreIds

) {
}
