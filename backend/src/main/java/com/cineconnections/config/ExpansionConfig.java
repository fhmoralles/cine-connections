package com.cineconnections.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "cine-connections.expansion")
public interface ExpansionConfig {

    int maxMoviesPerPerson();

    int maxCastPerMovie();

    boolean includeCrew();
}
