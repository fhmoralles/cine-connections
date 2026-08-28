package com.cineconnections.service;

import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbMovieCastMember;
import com.cineconnections.client.tmdb.dto.TmdbMovieCreditsResponse;
import com.cineconnections.client.tmdb.dto.TmdbMovieCrewMember;
import com.cineconnections.config.ExpansionConfig;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.entity.Credit;
import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.enumtype.CreditType;
import com.cineconnections.domain.repository.CreditRepository;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class MovieExpansionService {

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
    CreditRepository creditRepository;

    @Transactional
    public ExpansionResult expandMovie(Movie movie) {

        TmdbMovieCreditsResponse response =
                tmdbClient.getMovieCredits(
                        movie.tmdbId,
                        tmdbConfig.language()
                );

        int importedCast = importCast(
                movie,
                response
        );

        int importedCrew = 0;

        if (expansionConfig.includeCrew()) {
            importedCrew = importCrew(
                    movie,
                    response
            );
        }

        return new ExpansionResult(
                movie.id,
                movie.title,
                importedCast,
                importedCrew
        );
    }

    private int importCast(
            Movie movie,
            TmdbMovieCreditsResponse response
    ) {

        if (response.cast() == null) {
            return 0;
        }

        int imported = 0;

        for (TmdbMovieCastMember castMember :
                response.cast()
                        .stream()
                        .limit(expansionConfig.maxCastPerMovie())
                        .toList()) {

            Person person = personRepository.getOrCreate(
                    castMember.id(),
                    castMember.name(),
                    castMember.profilePath()
            );

            boolean exists = creditRepository.exists(
                    person.id,
                    movie.id,
                    CreditType.CAST,
                    castMember.character(),
                    null
            );

            if (exists) {
                continue;
            }

            Credit credit = new Credit();

            credit.person = person;
            credit.movie = movie;
            credit.creditType = CreditType.CAST;
            credit.characterName = castMember.character();

            creditRepository.persist(credit);

            imported++;
        }

        return imported;
    }

    private int importCrew(
            Movie movie,
            TmdbMovieCreditsResponse response
    ) {

        if (response.crew() == null) {
            return 0;
        }

        int imported = 0;

        for (TmdbMovieCrewMember crewMember : response.crew()) {

            Person person = personRepository.getOrCreate(
                    crewMember.id(),
                    crewMember.name(),
                    crewMember.profilePath()
            );

            boolean exists = creditRepository.exists(
                    person.id,
                    movie.id,
                    CreditType.CREW,
                    null,
                    crewMember.job()
            );

            if (exists) {
                continue;
            }

            Credit credit = new Credit();

            credit.person = person;
            credit.movie = movie;
            credit.creditType = CreditType.CREW;
            credit.department = crewMember.department();
            credit.job = crewMember.job();

            creditRepository.persist(credit);

            imported++;
        }

        return imported;
    }

    public record ExpansionResult(
            java.util.UUID movieId,
            String movieTitle,
            int importedCast,
            int importedCrew
    ) {
    }
}
