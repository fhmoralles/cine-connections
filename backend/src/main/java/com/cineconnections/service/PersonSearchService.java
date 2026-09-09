package com.cineconnections.service;

import com.cineconnections.client.tmdb.TmdbClient;
import com.cineconnections.client.tmdb.dto.TmdbPersonSearchResult;
import com.cineconnections.config.TmdbConfig;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PersonSearchService {

    private static final Logger LOG =
            Logger.getLogger(PersonSearchService.class);

    @Inject
    PersonRepository personRepository;

    @Inject
    @RestClient
    TmdbClient tmdbClient;

    @Inject
    TmdbConfig tmdbConfig;

    @Transactional
    public SearchResult search(String query) {

        List<SearchPersonResult> localResults =
                searchLocal(query);

        List<SearchPersonResult> tmdbResults =
                searchTmdb(query);

        return merge(localResults, tmdbResults);
    }

    private List<SearchPersonResult> searchLocal(String query) {

        return personRepository.searchByName(query)
                .stream()
                .map(person ->
                        SearchPersonResult.from(
                                person,
                                true
                        )
                )
                .toList();
    }

    private List<SearchPersonResult> searchTmdb(String query) {

        try {

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

            return tmdbResults.stream()
                    .map(SearchPersonResult::from)
                    .toList();

        } catch (Exception exception) {

            LOG.warnf(
                    exception,
                    "TMDB person search failed for query '%s'",
                    query
            );

            return List.of();
        }
    }

    private SearchResult merge(
            List<SearchPersonResult> localResults,
            List<SearchPersonResult> tmdbResults
    ) {

        Map<Long, SearchPersonResult> localByTmdbId =
                new LinkedHashMap<>();

        for (SearchPersonResult person : localResults) {
            if (person.tmdbId() != null) {
                localByTmdbId.put(person.tmdbId(), person);
            }
        }

        List<SearchPersonResult> results =
                new ArrayList<>();

        for (SearchPersonResult tmdbPerson : tmdbResults) {

            SearchPersonResult localPerson =
                    localByTmdbId.remove(tmdbPerson.tmdbId());

            results.add(
                    localPerson != null
                            ? localPerson
                            : tmdbPerson
            );
        }

        results.addAll(localByTmdbId.values());

        boolean imported = results.stream()
                .anyMatch(SearchPersonResult::imported);

        return new SearchResult(
                results.stream().limit(20).toList(),
                imported
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
