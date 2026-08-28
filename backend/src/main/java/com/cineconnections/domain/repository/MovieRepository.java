package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Movie;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MovieRepository
        implements PanacheRepositoryBase<Movie, UUID> {

    public Optional<Movie> findByTmdbId(Long tmdbId) {
        return find("tmdbId", tmdbId)
                .firstResultOptional();
    }

}
