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

    @SuppressWarnings("unchecked")
    public List<Object[]> findFrequentCoActors(
            UUID personId,
            int limit
    ) {

        return getEntityManager()
                .createNativeQuery("""
                        select p.id,
                               p.tmdb_id,
                               p.name,
                               p.profile_path,
                               count(distinct c2.movie_id) as shared
                        from credit c1
                        join credit c2
                            on c2.movie_id = c1.movie_id
                           and c2.person_id <> c1.person_id
                        join person p
                            on p.id = c2.person_id
                        where c1.person_id = :personId
                          and p.profile_path is not null
                          and p.profile_path <> ''
                        group by p.id, p.tmdb_id, p.name, p.profile_path
                        order by shared desc, p.name asc
                        """)
                .setParameter("personId", personId)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countCoActors(UUID personId) {

        Number count = (Number) getEntityManager()
                .createNativeQuery("""
                        select count(distinct c2.person_id)
                        from credit c1
                        join credit c2
                            on c2.movie_id = c1.movie_id
                           and c2.person_id <> c1.person_id
                        join person p
                            on p.id = c2.person_id
                        where c1.person_id = :personId
                          and p.profile_path is not null
                          and p.profile_path <> ''
                        """)
                .setParameter("personId", personId)
                .getSingleResult();

        return count == null ? 0 : count.longValue();
    }

}
