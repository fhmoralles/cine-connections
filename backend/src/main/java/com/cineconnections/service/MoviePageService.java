package com.cineconnections.service;

import com.cineconnections.api.dto.movie.MovieCastMemberResponse;
import com.cineconnections.api.dto.movie.MoviePageResponse;
import com.cineconnections.api.dto.movie.MovieSummaryResponse;
import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbGenre;
import com.cineconnections.client.tmdb.dto.TmdbMovieDetails;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.entity.Credit;
import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.repository.CreditRepository;
import com.cineconnections.domain.repository.MovieRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MoviePageService {

    private static final Logger LOG =
            Logger.getLogger(MoviePageService.class);

    private static final int SIMILAR_LIMIT = 16;

    private static final int OTHER_MOVIES_LIMIT = 24;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CreditRepository creditRepository;

    @Inject
    TmdbImportService tmdbImportService;

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    @Transactional
    public MoviePageResponse getMoviePage(UUID movieId) {

        Movie movie = movieRepository
                .findByIdOptional(movieId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Movie not found: " + movieId
                        )
                );

        if (movie.tmdbId != null) {
            movie = tmdbImportService.importMovieByTmdbId(movie.tmdbId);
        }

        ensureMovieDetails(movie);
        movieRepository.flush();

        List<Credit> castCredits =
                creditRepository.findCastByMovieId(movieId)
                        .stream()
                        .filter(credit ->
                                hasProfile(credit.person.profilePath)
                        )
                        .toList();

        List<UUID> actorIds = castCredits.stream()
                .map(credit -> credit.person.id)
                .distinct()
                .toList();

        List<Movie> similarMovies =
                movieRepository.findSimilarByGenre(
                        movieId,
                        SIMILAR_LIMIT
                );

        List<Movie> otherMovies =
                movieRepository.findOtherMoviesWithPeople(
                        movieId,
                        actorIds,
                        OTHER_MOVIES_LIMIT
                );

        return new MoviePageResponse(
                toSummary(movie),
                castCredits.stream()
                        .map(this::toCastMember)
                        .toList(),
                similarMovies.stream()
                        .map(this::toSummary)
                        .toList(),
                otherMovies.stream()
                        .map(this::toSummary)
                        .toList()
        );
    }

    private void ensureMovieDetails(Movie movie) {

        boolean missingGenres =
                movie.genreIds == null || movie.genreIds.isEmpty();

        boolean missingBackdrop =
                movie.backdropPath == null
                        || movie.backdropPath.isBlank();

        if (!missingGenres && !missingBackdrop) {
            return;
        }

        if (movie.tmdbId == null) {
            return;
        }

        try {

            TmdbMovieDetails details =
                    tmdbClient.getMovie(
                            movie.tmdbId,
                            tmdbConfig.language()
                    );

            if (details.backdropPath() != null) {
                movie.backdropPath = details.backdropPath();
            }

            if (details.posterPath() != null) {
                movie.posterPath = details.posterPath();
            }

            if (details.genres() != null) {
                movie.addGenreIds(
                        details.genres()
                                .stream()
                                .map(TmdbGenre::id)
                                .toList()
                );
            }

        } catch (Exception exception) {

            LOG.warnf(
                    exception,
                    "Unable to complete movie details for tmdbId=%s",
                    movie.tmdbId
            );
        }
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

    private MovieCastMemberResponse toCastMember(Credit credit) {

        return new MovieCastMemberResponse(
                credit.person.id,
                credit.person.tmdbId,
                credit.person.name,
                credit.characterName,
                credit.person.profilePath,
                credit.castOrder
        );
    }

    private boolean hasProfile(String profilePath) {

        return profilePath != null && !profilePath.isBlank();
    }
}
