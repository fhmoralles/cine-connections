package com.cineconnections.service;

import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbCastCredit;
import com.cineconnections.client.tmdb.dto.TmdbCrewCredit;
import com.cineconnections.client.tmdb.dto.TmdbPersonDetails;
import com.cineconnections.client.tmdb.dto.TmdbPersonMovieCreditsResponse;
import com.cineconnections.client.tmdb.dto.TmdbPersonSearchResult;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.entity.Credit;
import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.enumtype.CreditType;
import com.cineconnections.domain.repository.CreditRepository;
import com.cineconnections.domain.repository.MovieRepository;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Comparator;

@ApplicationScoped
public class TmdbImportService {

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CreditRepository creditRepository;

    @Transactional
    public Person importPerson(String name) {

        TmdbPersonSearchResult searchResult =
                findBestPerson(name);

        TmdbPersonDetails tmdbPerson =
                tmdbClient.getPerson(
                        searchResult.id(),
                        tmdbConfig.language()
                );

        Person person = personRepository
                .findByTmdbId(tmdbPerson.id())
                .orElseGet(Person::new);

        person.tmdbId = tmdbPerson.id();
        person.name = tmdbPerson.name();
        person.profilePath = tmdbPerson.profilePath();

        personRepository.persist(person);

        importMovieCredits(person);

        return person;
    }

    private TmdbPersonSearchResult findBestPerson(String name) {

        var response = tmdbClient.searchPerson(
                name,
                tmdbConfig.language(),
                1,
                false
        );

        return response.results()
                .stream()
                .max(Comparator.comparingDouble(
                        TmdbPersonSearchResult::popularity
                ))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Person not found: " + name
                        )
                );
    }

    private void importMovieCredits(Person person) {

        TmdbPersonMovieCreditsResponse credits =
                tmdbClient.getMovieCredits(
                        person.tmdbId,
                        tmdbConfig.language()
                );

        credits.cast()
                .forEach(castCredit ->
                        importCastCredit(person, castCredit)
                );

        credits.crew()
                .forEach(crewCredit ->
                        importCrewCredit(person, crewCredit)
                );
    }

    private void importCastCredit(
            Person person,
            TmdbCastCredit tmdbCredit
    ) {

        Movie movie = getOrCreateMovie(
                tmdbCredit.id(),
                tmdbCredit.title(),
                tmdbCredit.releaseDate(),
                tmdbCredit.posterPath()
        );

        boolean exists = creditRepository.exists(
                person.id,
                movie.id,
                CreditType.CAST,
                tmdbCredit.character(),
                null
        );

        if (exists) {
            return;
        }

        Credit credit = new Credit();

        credit.person = person;
        credit.movie = movie;
        credit.creditType = CreditType.CAST;
        credit.characterName = tmdbCredit.character();

        creditRepository.persist(credit);
    }

    private void importCrewCredit(
            Person person,
            TmdbCrewCredit tmdbCredit
    ) {

        Movie movie = getOrCreateMovie(
                tmdbCredit.id(),
                tmdbCredit.title(),
                tmdbCredit.releaseDate(),
                tmdbCredit.posterPath()
        );

        boolean exists = creditRepository.exists(
                person.id,
                movie.id,
                CreditType.CREW,
                null,
                tmdbCredit.job()
        );

        if (exists) {
            return;
        }

        Credit credit = new Credit();

        credit.person = person;
        credit.movie = movie;
        credit.creditType = CreditType.CREW;
        credit.department = tmdbCredit.department();
        credit.job = tmdbCredit.job();

        creditRepository.persist(credit);
    }

    private Movie getOrCreateMovie(
            long tmdbId,
            String title,
            java.time.LocalDate releaseDate,
            String posterPath
    ) {

        return movieRepository
                .findByTmdbId(tmdbId)
                .orElseGet(() -> {

                    Movie movie = new Movie();

                    movie.tmdbId = tmdbId;
                    movie.title = title;
                    movie.releaseDate = releaseDate;
                    movie.posterPath = posterPath;

                    movieRepository.persist(movie);

                    return movie;
                });
    }
}
