package com.cineconnections.client.ipgeo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "ip-geo-api")
public interface IpGeoClient {

    @GET
    @Path("/{ip}")
    @Produces(MediaType.APPLICATION_JSON)
    IpGeoResponse lookup(@PathParam("ip") String ip);
}
