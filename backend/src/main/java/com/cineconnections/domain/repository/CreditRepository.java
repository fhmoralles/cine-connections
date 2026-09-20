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

    public List<Credit> findCastByMovieId(UUID movieId) {

        return getEntityManager()
                .createQuery("""
                        select c
                        from Credit c
                        join fetch c.person
                        where c.movie.id = :movieId
                          and c.creditType = com.cineconnections.domain.enumtype.CreditType.CAST
                        order by c.castOrder asc nulls last,
                                 c.createdAt asc
                        """, Credit.class)
                .setParameter("movieId", movieId)
                .getResultList();
    }

    public List<Credit> findByMovieId(UUID movieId) {
        return getEntityManager()
                .createQuery("""
                        select distinct c
                        from Credit c
                        join fetch c.person
                        join fetch c.movie
                        where c.movie.id = :movieId
                        """, Credit.class)
                .setParameter("movieId", movieId)
                .getResultList();
    }

    public List<Credit> findGraphCreditsByMovieId(UUID movieId) {

        return getEntityManager()
                .createQuery("""
                        select distinct c
                        from Credit c
                        join fetch c.person
                        join fetch c.movie
                        where c.person.id in (
                            select rc.person.id
                            from Credit rc
                            where rc.movie.id = :movieId
                        )
                        """, Credit.class)
                .setParameter("movieId", movieId)
                .getResultList();
    }

    public List<Credit> findByMovieIds(List<UUID> movieIds) {

        if (movieIds == null || movieIds.isEmpty()) {
            return List.of();
        }

        return find(
                "movie.id in ?1",
                movieIds
        ).list();
    }

    public List<Credit> findGraphCredits(UUID personId) {

        return getEntityManager()
                .createQuery("""
                        select distinct c
                        from Credit c
                        join fetch c.person
                        join fetch c.movie
                        where c.movie.id in (
                            select rc.movie.id
                            from Credit rc
                            where rc.person.id = :personId
                        )
                        """, Credit.class)
                .setParameter("personId", personId)
                .getResultList();
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
