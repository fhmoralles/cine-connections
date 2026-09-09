package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Person;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PersonRepository
        implements PanacheRepositoryBase<Person, UUID> {

    public Optional<Person> findByTmdbId(Long tmdbId) {
        return find("tmdbId", tmdbId)
                .firstResultOptional();
    }

    public Person getOrCreate(
            Long tmdbId,
            String name,
            String profilePath
    ) {

        return findByTmdbId(tmdbId)
                .orElseGet(() -> {

                    Person person =
                            Person.fromTmdbCastMember(
                                    tmdbId,
                                    name,
                                    profilePath
                            );

                    persist(person);

                    return person;
                });
    }

    public Person saveOrUpdate(
            Long tmdbId,
            String name,
            String profilePath
    ) {

        Person person =
                findByTmdbId(tmdbId)
                        .orElse(null);

        if (person == null) {

            person = new Person();

            person.tmdbId = tmdbId;
            person.name = name;
            person.profilePath = profilePath;

            persist(person);

        } else {

            person.name = name;

            if (profilePath != null) {
                person.profilePath = profilePath;
            }
        }

        return person;
    }

    public List<Person> searchByName(
            String query
    ) {

        return find(
                "lower(name) like lower(?1)",
                "%" + query + "%"
        )
        .page(0, 20)
        .list();
    }
}
