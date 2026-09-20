package com.cineconnections.domain.repository;

import com.cineconnections.domain.entity.Movie;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MovieRepository
        implements PanacheRepositoryBase<Movie, UUID> {

    public Optional<Movie> findByTmdbId(Long tmdbId) {
        return find("tmdbId", tmdbId)
                .firstResultOptional();
    }

    public List<Movie> searchByTitle(String query) {
        return find(
                "lower(title) like lower(?1)",
                "%" + query + "%"
        )
        .page(0, 10)
        .list();
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findSimilarByGenre(
            UUID movieId,
            int limit
    ) {

        return getEntityManager()
                .createNativeQuery("""
                        select distinct m.*
                        from movie m
                        join movie_genre g
                            on g.movie_id = m.id
                        where g.tmdb_genre_id in (
                            select g2.tmdb_genre_id
                            from movie_genre g2
                            where g2.movie_id = :movieId
                        )
                        and m.id <> :movieId
                        and m.poster_path is not null
                        order by m.release_date desc nulls last
                        """, Movie.class)
                .setParameter("movieId", movieId)
                .setMaxResults(limit)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findOtherMoviesWithPeople(
            UUID movieId,
            List<UUID> personIds,
            int limit
    ) {

        if (personIds == null || personIds.isEmpty()) {
            return List.of();
        }

        return getEntityManager()
                .createNativeQuery("""
                        select m.*
                        from movie m
                        join (
                            select c.movie_id,
                                   count(distinct c.person_id) as shared
                            from credit c
                            where c.person_id in (:personIds)
                              and c.credit_type = 'CAST'
                              and c.movie_id <> :movieId
                            group by c.movie_id
                        ) shared_movies
                            on shared_movies.movie_id = m.id
                        where m.poster_path is not null
                        order by shared_movies.shared desc,
                                 m.release_date desc nulls last
                        """, Movie.class)
                .setParameter("movieId", movieId)
                .setParameter("personIds", personIds)
                .setMaxResults(limit)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findByPersonId(UUID personId) {

        return getEntityManager()
                .createNativeQuery("""
                        select distinct m.*
                        from movie m
                        join credit c
                            on c.movie_id = m.id
                        where c.person_id = :personId
                        order by m.release_date desc nulls last
                        """, Movie.class)
                .setParameter("personId", personId)
                .getResultList();
    }

}
