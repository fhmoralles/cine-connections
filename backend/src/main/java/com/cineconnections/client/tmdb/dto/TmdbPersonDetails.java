package com.cineconnections.client.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record TmdbPersonDetails(

        long id,

        String name,

        String biography,

        @JsonProperty("birthday")
        LocalDate birthday,

        @JsonProperty("profile_path")
        String profilePath,

        @JsonProperty("known_for_department")
        String knownForDepartment

) {
}
