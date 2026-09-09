package com.cineconnections.service;

import com.cineconnections.api.dto.search.SearchResponse;
import com.cineconnections.api.dto.search.SearchResult;
import com.cineconnections.api.dto.search.SearchResultType;
import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.repository.MovieRepository;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SearchService {

    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    public SearchResponse search(String query) {

        String normalized =
                query == null
                        ? ""
                        : query.trim();

        if (normalized.length() < 2) {
            return new SearchResponse(List.of());
        }

        List<SearchResult> results = new ArrayList<>();

        searchLocalPeople(normalized, results);
        searchLocalMovies(normalized, results);

        if (results.isEmpty()) {
            searchTmdbPeople(normalized, results);
            searchTmdbMovies(normalized, results);
        }

        return new SearchResponse(results);
    }

    private void searchLocalPeople(
            String query,
            List<SearchResult> results
    ) {

        personRepository
                .searchByName(query)
                .forEach(person ->
                        results.add(
                                new SearchResult(
                                        SearchResultType.PERSON,
                                        person.id.toString(),
                                        person.tmdbId,
                                        person.name,
                                        "Person",
                                        person.profilePath,
                                        true
                                )
                        )
                );
    }

    private void searchLocalMovies(
            String query,
            List<SearchResult> results
    ) {

        movieRepository
                .searchByTitle(query)
                .forEach(movie ->
                        results.add(
                                new SearchResult(
                                        SearchResultType.MOVIE,
                                        movie.id.toString(),
                                        movie.tmdbId,
                                        movie.title,
                                        extractYearFromLocalDate(movie.releaseDate),
                                        movie.posterPath,
                                        true
                                )
                        )
                );
    }

    private void searchTmdbPeople(
            String query,
            List<SearchResult> results
    ) {

        var response =
                tmdbClient.searchPerson(
                        query,
                        tmdbConfig.language(),
                        1,
                        false
                );

        if (response.results() == null) {
            return;
        }

        response.results()
                .stream()
                .limit(10)
                .forEach(person ->
                        results.add(
                                new SearchResult(
                                        SearchResultType.PERSON,
                                        null,
                                        person.id(),
                                        person.name(),
                                        "Person",
                                        person.profilePath(),
                                        false
                                )
                        )
                );
    }

    private void searchTmdbMovies(
            String query,
            List<SearchResult> results
    ) {

        var response =
                tmdbClient.searchMovies(
                        query,
                        tmdbConfig.language(),
                        1,
                        false
                );

        if (response.results() == null) {
            return;
        }

        response.results()
                .stream()
                .limit(10)
                .forEach(movie ->
                        results.add(
                                new SearchResult(
                                        SearchResultType.MOVIE,
                                        null,
                                        movie.id(),
                                        movie.title(),
                                        extractYear(movie.releaseDate()),
                                        movie.posterPath(),
                                        false
                                )
                        )
                );
    }

    private String extractYearFromLocalDate(LocalDate releaseDate) {

        if (releaseDate == null) {
            return "Movie";
        }

        return String.valueOf(releaseDate.getYear());
    }

    private String extractYear(String releaseDate) {

        if (releaseDate == null || releaseDate.length() < 4) {
            return "Movie";
        }

        return releaseDate.substring(0, 4);
    }
}
