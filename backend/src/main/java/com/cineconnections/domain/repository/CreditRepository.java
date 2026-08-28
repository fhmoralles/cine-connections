package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Credit;
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

}
