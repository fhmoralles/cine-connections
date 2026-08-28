package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Credit;
import com.cineconnections.domain.enumtype.CreditType;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CreditRepository
        implements PanacheRepositoryBase<Credit, UUID> {

    public List<Credit> findByPersonId(UUID personId) {
        return find("person.id", personId).list();
    }

    public List<Credit> findByMovieId(UUID movieId) {
        return find("movie.id", movieId).list();
    }

    public boolean exists(
            UUID personId,
            UUID movieId,
            CreditType creditType,
            String characterName,
            String job
    ) {

        String query = """
                person.id = ?1
                and movie.id = ?2
                and creditType = ?3
                and characterName is not distinct from ?4
                and job is not distinct from ?5
                """;

        return count(
                query,
                personId,
                movieId,
                creditType,
                characterName,
                job
        ) > 0;
    }
}
