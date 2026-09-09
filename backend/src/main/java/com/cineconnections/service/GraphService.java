package com.cineconnections.service;

import com.cineconnections.api.dto.graph.GraphEdge;
import com.cineconnections.api.dto.graph.GraphEdgeType;
import com.cineconnections.api.dto.graph.GraphNode;
import com.cineconnections.api.dto.graph.GraphNodeType;
import com.cineconnections.api.dto.graph.GraphResponse;
import com.cineconnections.domain.entity.Credit;
import com.cineconnections.domain.entity.Movie;
import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.repository.CreditRepository;
import com.cineconnections.domain.repository.MovieRepository;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class GraphService {

    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CreditRepository creditRepository;

    @Transactional
    public GraphResponse getPersonGraph(UUID personId, int depth) {

        Person rootPerson = personRepository
                .findByIdOptional(personId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Person not found: " + personId
                        )
                );

        List<Credit> credits = depth <= 1
                ? creditRepository.findByPersonId(personId)
                : creditRepository.findGraphCredits(personId);

        Map<String, GraphNode> nodes =
                new LinkedHashMap<>();

        Map<String, GraphEdge> edges =
                new LinkedHashMap<>();

        GraphNode rootNode =
                toPersonNode(rootPerson);

        nodes.put(rootNode.id(), rootNode);

        for (Credit credit : credits) {

            if (depth <= 1
                    && !credit.person.id.equals(personId)) {
                continue;
            }

            Person person = credit.person;
            Movie movie = credit.movie;

            GraphNode personNode =
                    toPersonNode(person);

            GraphNode movieNode =
                    toMovieNode(movie);

            nodes.putIfAbsent(
                    personNode.id(),
                    personNode
            );

            nodes.putIfAbsent(
                    movieNode.id(),
                    movieNode
            );

            GraphEdge edge =
                    toCreditEdge(credit);

            edges.putIfAbsent(
                    edge.id(),
                    edge
            );
        }

        return new GraphResponse(
                rootNode,
                new ArrayList<>(nodes.values()),
                new ArrayList<>(edges.values())
        );
    }

    @Transactional
    public GraphResponse buildMovieGraph(UUID movieId, int depth) {

        Movie rootMovie = movieRepository
                .findByIdOptional(movieId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Movie not found: " + movieId
                        )
                );

        List<Credit> credits = depth <= 1
                ? creditRepository.findByMovieId(movieId)
                : creditRepository.findGraphCreditsByMovieId(movieId);

        Map<String, GraphNode> nodes =
                new LinkedHashMap<>();

        Map<String, GraphEdge> edges =
                new LinkedHashMap<>();

        GraphNode rootNode =
                toMovieNode(rootMovie);

        nodes.put(rootNode.id(), rootNode);

        for (Credit credit : credits) {

            if (depth <= 1
                    && !credit.movie.id.equals(movieId)) {
                continue;
            }

            Person person = credit.person;
            Movie movie = credit.movie;

            GraphNode personNode =
                    toPersonNode(person);

            GraphNode movieNode =
                    toMovieNode(movie);

            nodes.putIfAbsent(
                    personNode.id(),
                    personNode
            );

            nodes.putIfAbsent(
                    movieNode.id(),
                    movieNode
            );

            GraphEdge edge =
                    toCreditEdge(credit);

            edges.putIfAbsent(
                    edge.id(),
                    edge
            );
        }

        return new GraphResponse(
                rootNode,
                new ArrayList<>(nodes.values()),
                new ArrayList<>(edges.values())
        );
    }

    private GraphNode toPersonNode(Person person) {

        return new GraphNode(
                "person-" + person.id,
                GraphNodeType.PERSON,
                person.name,
                person.profilePath,
                person.tmdbId
        );
    }

    private GraphNode toMovieNode(Movie movie) {

        return new GraphNode(
                "movie-" + movie.id,
                GraphNodeType.MOVIE,
                movie.title,
                movie.posterPath,
                movie.tmdbId
        );
    }

    private GraphEdge toCreditEdge(Credit credit) {

        return new GraphEdge(
                "credit-" + credit.id,
                "person-" + credit.person.id,
                "movie-" + credit.movie.id,
                GraphEdgeType.CREDIT,
                credit.characterName,
                credit.job
        );
    }
}
