package com.cineconnections.service;

import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbPersonSearchResult;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class PersonSearchService {

    @Inject
    PersonRepository personRepository;

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    public SearchResult search(String query) {

        List<Person> localResults =
                personRepository.searchByName(query);

        if (!localResults.isEmpty()) {

            return new SearchResult(
                    localResults.stream()
                            .map(person ->
                                    SearchPersonResult.from(
                                            person,
                                            true
                                    )
                            )
                            .toList(),
                    true
            );
        }

        var response =
                tmdbClient.searchPerson(
                        query,
                        tmdbConfig.language(),
                        1,
                        false
                );

        List<TmdbPersonSearchResult> tmdbResults =
                response.results() != null
                        ? response.results()
                        : List.of();

        return new SearchResult(
                tmdbResults.stream()
                        .map(SearchPersonResult::from)
                        .toList(),
                false
        );
    }

    public record SearchResult(
            List<SearchPersonResult> results,
            boolean imported
    ) {
    }

    public record SearchPersonResult(
            String id,
            Long tmdbId,
            String name,
            String profilePath,
            boolean imported
    ) {

        static SearchPersonResult from(
                Person person,
                boolean imported
        ) {

            return new SearchPersonResult(
                    person.id.toString(),
                    person.tmdbId,
                    person.name,
                    person.profilePath,
                    imported
            );
        }

        static SearchPersonResult from(
                TmdbPersonSearchResult result
        ) {

            return new SearchPersonResult(
                    null,
                    result.id(),
                    result.name(),
                    result.profilePath(),
                    false
            );
        }
    }
}
