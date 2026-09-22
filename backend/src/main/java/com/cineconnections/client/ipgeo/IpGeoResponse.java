package com.cineconnections.client.ipgeo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IpGeoResponse(
        boolean success,
        String ip,
        String city,
        String region,
        String country,
        @JsonProperty("country_code") String countryCode,
        String continent,
        String message
) {
}
