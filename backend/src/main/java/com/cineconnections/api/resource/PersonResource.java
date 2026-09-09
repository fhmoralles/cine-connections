package com.cineconnections.api.resource;

import com.cineconnections.domain.entity.Person;
import com.cineconnections.domain.repository.PersonRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    PersonRepository personRepository;

    @GET
    @Path("/search")
    public List<PersonResponse> search(
            @QueryParam("query") String query
    ) {

        if (query == null || query.isBlank()) {
            return List.of();
        }

        return personRepository
                .searchByName(query)
                .stream()
                .map(PersonResponse::from)
                .toList();
    }

    public record PersonResponse(
            UUID id,
            Long tmdbId,
            String name,
            String profilePath
    ) {

        public static PersonResponse from(Person person) {

            return new PersonResponse(
                    person.id,
                    person.tmdbId,
                    person.name,
                    person.profilePath
            );
        }
    }
}
