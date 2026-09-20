package com.cineconnections.service;

import com.cineconnections.api.dto.movie.MovieSummaryResponse;
import com.cineconnections.api.dto.person.CoActorResponse;
import com.cineconnections.api.dto.person.PersonPageResponse;
import com.cineconnections.api.dto.person.PersonProfileResponse;
import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.repository.MovieRepository;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PersonPageService {

    private static final int CO_ACTOR_LIMIT = 24;

    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    TmdbImportService tmdbImportService;

    @Transactional
    public PersonPageResponse getPersonPage(UUID personId) {

        Person person = personRepository
                .findByIdOptional(personId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Person not found: " + personId
                        )
                );

        if (person.tmdbId != null) {
            person = tmdbImportService.importPersonByTmdbId(person.tmdbId);
            personRepository.flush();
        }

        List<MovieSummaryResponse> movies =
                movieRepository.findByPersonId(personId)
                        .stream()
                        .map(this::toSummary)
                        .toList();

        List<CoActorResponse> coActors =
                personRepository.findFrequentCoActors(
                                personId,
                                CO_ACTOR_LIMIT
                        )
                        .stream()
                        .map(this::toCoActor)
                        .toList();

        long coActorCount =
                personRepository.countCoActors(personId);

        return new PersonPageResponse(
                new PersonProfileResponse(
                        person.id,
                        person.tmdbId,
                        person.name,
                        person.profilePath
                ),
                movies,
                coActors,
                movies.size(),
                coActorCount
        );
    }

    private MovieSummaryResponse toSummary(Movie movie) {

        Integer releaseYear = movie.releaseDate == null
                ? null
                : movie.releaseDate.getYear();

        return new MovieSummaryResponse(
                movie.id,
                movie.tmdbId,
                movie.title,
                releaseYear,
                movie.posterPath,
                movie.backdropPath
        );
    }

    private CoActorResponse toCoActor(Object[] row) {

        return new CoActorResponse(
                toUuid(row[0]),
                toLong(row[1]),
                row[2] == null ? "" : row[2].toString(),
                row[3] == null ? null : row[3].toString(),
                toLong(row[4]) == null ? 0 : toLong(row[4])
        );
    }

    private UUID toUuid(Object value) {

        if (value instanceof UUID uuid) {
            return uuid;
        }

        return UUID.fromString(value.toString());
    }

    private Long toLong(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.parseLong(value.toString());
    }
}
