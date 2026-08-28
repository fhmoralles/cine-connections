package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Person;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PersonRepository
        implements PanacheRepositoryBase<Person, UUID> {

    public Optional<Person> findByTmdbId(Long tmdbId) {
        return find("tmdbId", tmdbId)
                .firstResultOptional();
    }

}
