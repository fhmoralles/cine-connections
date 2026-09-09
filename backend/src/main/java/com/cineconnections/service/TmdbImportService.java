package com.cineconnections.service;

import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbCastCredit;
import com.cineconnections.client.tmdb.dto.TmdbCrewCredit;
import com.cineconnections.client.tmdb.dto.TmdbMovieDetails;
import com.cineconnections.client.tmdb.dto.TmdbPersonDetails;
import com.cineconnections.client.tmdb.dto.TmdbPersonMovieCreditsResponse;
import com.cineconnections.client.tmdb.dto.TmdbPersonSearchResult;
import com.cineconnections.config.ExpansionConfig;
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class TmdbImportService {

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    @Inject
    ExpansionConfig expansionConfig;

    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CreditRepository creditRepository;

    @Inject
    MovieExpansionService movieExpansionService;

    @Transactional
    public Person importPerson(String name) {

        TmdbPersonSearchResult searchResult =
                findBestPerson(name);

        TmdbPersonDetails tmdbPerson =
                tmdbClient.getPerson(
                        searchResult.id(),
                        tmdbConfig.language()
                );

        Person person =
                personRepository.saveOrUpdate(
                        tmdbPerson.id(),
                        tmdbPerson.name(),
                        tmdbPerson.profilePath()
                );

        importMovieCredits(person);

        return person;
    }

    @Transactional
    public Person importPersonByTmdbId(
            long tmdbId
    ) {

        TmdbPersonDetails tmdbPerson =
                tmdbClient.getPerson(
                        tmdbId,
                        tmdbConfig.language()
                );

        Person person =
                personRepository.saveOrUpdate(
                        tmdbPerson.id(),
                        tmdbPerson.name(),
                        tmdbPerson.profilePath()
                );

        importMovieCredits(person);

        return person;
    }

    @Transactional
    public Movie importMovieByTmdbId(long tmdbId) {

        TmdbMovieDetails details =
                tmdbClient.getMovie(
                        tmdbId,
                        tmdbConfig.language()
                );

        Movie movie =
                movieRepository
                        .findByTmdbId(tmdbId)
                        .orElse(null);

        if (movie == null) {

            movie = new Movie();

            movie.tmdbId = details.id();
            movie.title = details.title();
            movie.posterPath = details.posterPath();
            movie.releaseDate = parseReleaseDate(details.releaseDate());

            movieRepository.persist(movie);

        } else {

            movie.title = details.title();

            if (details.posterPath() != null) {
                movie.posterPath = details.posterPath();
            }

            if (details.releaseDate() != null) {
                movie.releaseDate = parseReleaseDate(details.releaseDate());
            }
        }

        movieExpansionService.expandMovie(movie);

        return movie;
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
                tmdbClient.getPersonMovieCredits(
                        person.tmdbId,
                        tmdbConfig.language()
                );

        Set<Long> expandedMovies = new HashSet<>();
        int maxMovies = expansionConfig.maxMoviesPerPerson();

        List<TmdbCastCredit> castCredits =
                credits.cast() != null ? credits.cast() : List.of();

        castCredits.forEach(castCredit ->
                importCastCredit(
                        person,
                        castCredit,
                        expandedMovies,
                        maxMovies
                )
        );

        List<TmdbCrewCredit> crewCredits =
                credits.crew() != null ? credits.crew() : List.of();

        crewCredits.forEach(crewCredit ->
                importCrewCredit(
                        person,
                        crewCredit,
                        expandedMovies,
                        maxMovies
                )
        );
    }

    private void importCastCredit(
            Person person,
            TmdbCastCredit tmdbCredit,
            Set<Long> expandedMovies,
            int maxMovies
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

        if (!exists) {

            Credit credit = new Credit();

            credit.person = person;
            credit.movie = movie;
            credit.creditType = CreditType.CAST;
            credit.characterName = tmdbCredit.character();

            creditRepository.persist(credit);
        }

        expandMovieIfAllowed(movie, expandedMovies, maxMovies);
    }

    private void importCrewCredit(
            Person person,
            TmdbCrewCredit tmdbCredit,
            Set<Long> expandedMovies,
            int maxMovies
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

        if (!exists) {

            Credit credit = new Credit();

            credit.person = person;
            credit.movie = movie;
            credit.creditType = CreditType.CREW;
            credit.department = tmdbCredit.department();
            credit.job = tmdbCredit.job();

            creditRepository.persist(credit);
        }

        expandMovieIfAllowed(movie, expandedMovies, maxMovies);
    }

    private void expandMovieIfAllowed(
            Movie movie,
            Set<Long> expandedMovies,
            int maxMovies
    ) {

        if (expandedMovies.size() >= maxMovies) {
            return;
        }

        if (!expandedMovies.add(movie.tmdbId)) {
            return;
        }

        movieExpansionService.expandMovie(movie);
    }

    private Movie getOrCreateMovie(
            long tmdbId,
            String title,
            LocalDate releaseDate,
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

    private LocalDate parseReleaseDate(String releaseDate) {

        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(releaseDate);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
