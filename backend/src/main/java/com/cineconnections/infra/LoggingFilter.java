package com.cineconnections.infra;

import com.cineconnections.service.LogRequestService;

import io.vertx.core.http.HttpServerRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Inject
    LogRequestService logRequestService;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        final String method = context.getMethod();
        final String path = info.getPath();
        final String address = request.remoteAddress() != null
                ? request.remoteAddress().toString()
                : "unknown";

        LOG.infof("Request %s %s from IP %s", method, path, address);

        if (shouldSkip(path, method)) {
            return;
        }

        context.setProperty("logBody", LogRequestService.readRequestBody(context));
        context.setProperty("startTime", System.currentTimeMillis());
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) {
        Long startTime = (Long) requestContext.getProperty("startTime");
        if (startTime == null) {
            return;
        }

        Integer duration = (int) (System.currentTimeMillis() - startTime);
        LOG.infof(
                "Request %s %s from IP %s took %dms",
                requestContext.getMethod(),
                requestContext.getUriInfo().getPath(),
                request.remoteAddress() != null
                        ? request.remoteAddress().toString()
                        : "unknown",
                duration
        );

        logRequestService.create(requestContext, request, info, duration);
    }

    private boolean shouldSkip(String path, String method) {
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        return path.contains("/admin")
                || path.contains("/auth")
                || path.contains("/health")
                || path.contains("/q/");
    }
}
