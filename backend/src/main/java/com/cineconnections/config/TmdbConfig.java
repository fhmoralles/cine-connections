package com.cineconnections.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "tmdb.api")
public interface TmdbConfig {

    String baseUrl();

    String token();

    String language();
}
