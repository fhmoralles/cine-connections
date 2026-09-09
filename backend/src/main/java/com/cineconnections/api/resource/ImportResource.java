package com.cineconnections.api.resource;

import com.cineconnections.domain.entity.Person;
import com.cineconnections.service.TmdbImportService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/import")
@Produces(MediaType.APPLICATION_JSON)
public class ImportResource {

    @Inject
    TmdbImportService tmdbImportService;

    @POST
    @Path("/person")
    public ImportPersonResponse importPerson(
            @QueryParam("name") String name
    ) {

        Person person =
                tmdbImportService.importPerson(name);

        return new ImportPersonResponse(
                person.id,
                person.tmdbId,
                person.name
        );
    }

    @POST
    @Path("/person/{tmdbId}")
    public Response importPersonByTmdbId(
            @PathParam("tmdbId") long tmdbId
    ) {

        try {

            Person person =
                    tmdbImportService.importPersonByTmdbId(
                            tmdbId
                    );

            return Response.ok(
                    new ImportPersonResponse(
                            person.id,
                            person.tmdbId,
                            person.name
                    )
            ).build();

        } catch (Exception exception) {

            return Response
                    .status(Response.Status.BAD_GATEWAY)
                    .entity(new ErrorResponse(
                            "Unable to import person"
                    ))
                    .build();
        }
    }

    public record ImportPersonResponse(
            java.util.UUID id,
            Long tmdbId,
            String name
    ) {
    }

    public record ErrorResponse(
            String message
    ) {
    }
}
