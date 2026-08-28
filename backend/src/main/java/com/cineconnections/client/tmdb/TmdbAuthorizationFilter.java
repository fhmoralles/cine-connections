package com.cineconnections.client.tmdb;

import com.cineconnections.config.TmdbConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

@ApplicationScoped
public class TmdbAuthorizationFilter implements ClientRequestFilter {

    @Inject
    TmdbConfig tmdbConfig;

    @Override
    public void filter(ClientRequestContext requestContext) {

        requestContext.getHeaders().add(
                "Authorization",
                "Bearer " + tmdbConfig.token()
        );
    }
}
