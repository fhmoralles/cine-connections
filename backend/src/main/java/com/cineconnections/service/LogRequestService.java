package com.cineconnections.service;

import com.cineconnections.client.ipgeo.IpGeoClient;
import com.cineconnections.client.ipgeo.IpGeoResponse;
import com.cineconnections.domain.entity.LogRequest;
import com.cineconnections.domain.repository.LogRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.vertx.core.http.HttpServerRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class LogRequestService {

    private static final Logger LOG = Logger.getLogger(LogRequestService.class);

    private static final int MAX_BODY_LENGTH = 10_000;

    private static final int MAX_FIELD_LENGTH = 8_000;

    @Inject
    LogRequestRepository logRequestRepository;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @RestClient
    IpGeoClient ipGeoClient;

    public static String readRequestBody(ContainerRequestContext context) {
        if (!context.hasEntity()) {
            return null;
        }

        try {
            InputStream entityStream = context.getEntityStream();
            byte[] bytes = entityStream.readAllBytes();
            context.setEntityStream(new ByteArrayInputStream(bytes));

            if (bytes.length == 0) {
                return null;
            }

            String body = new String(bytes, StandardCharsets.UTF_8);
            return truncate(body, MAX_BODY_LENGTH);
        } catch (IOException e) {
            LOG.debug("Failed to read request body for logging", e);
            return null;
        }
    }

    public void create(
            ContainerRequestContext requestContext,
            HttpServerRequest request,
            UriInfo info,
            Integer duration
    ) {
        try {
            String remoteAddress = resolveRemoteAddress(requestContext, request);

            LogRequest logRequest = new LogRequest();
            logRequest.contextMethod = requestContext.getMethod();
            logRequest.contextHeaders = truncate(
                    toJson(headersAsMap(requestContext.getHeaders())),
                    MAX_FIELD_LENGTH
            );
            logRequest.contextBody = (String) requestContext.getProperty("logBody");
            logRequest.requestRemoteAddress = remoteAddress;
            logRequest.requestCookieMap = truncate(
                    toJson(cookiesAsMap(requestContext.getCookies())),
                    MAX_FIELD_LENGTH
            );
            logRequest.infoPath = info.getPath();
            logRequest.infoPathParameters = truncate(
                    toJson(multivaluedMapAsMap(info.getPathParameters())),
                    MAX_FIELD_LENGTH
            );
            logRequest.infoQueryParameters = truncate(
                    toJson(multivaluedMapAsMap(info.getQueryParameters())),
                    MAX_FIELD_LENGTH
            );
            logRequest.duration = duration;
            logRequest.requestOrigin = resolveOrigin(remoteAddress);

            QuarkusTransaction.requiringNew().run(() ->
                    logRequestRepository.persist(logRequest)
            );
        } catch (Exception e) {
            LOG.warnf(e, "Failed to persist request log for %s %s",
                    requestContext.getMethod(),
                    info.getPath());
        }
    }

    String resolveRemoteAddress(
            ContainerRequestContext requestContext,
            HttpServerRequest request
    ) {
        String forwardedFor = firstHeader(requestContext, "X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = firstHeader(requestContext, "X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        if (request != null && request.remoteAddress() != null) {
            String host = request.remoteAddress().host();
            if (host != null && !host.isBlank()) {
                return host.trim();
            }
            return request.remoteAddress().toString();
        }

        return null;
    }

    String resolveOrigin(String ipAddress) {
        String ip = normalizeIp(ipAddress);
        if (ip == null || isPrivateOrLocal(ip)) {
            return null;
        }

        try {
            IpGeoResponse response = ipGeoClient.lookup(ip);
            if (response == null || !response.success()) {
                return null;
            }

            return formatOrigin(response);
        } catch (Exception e) {
            LOG.debugf(e, "Failed to resolve origin for IP %s", ip);
            return null;
        }
    }

    private String formatOrigin(IpGeoResponse response) {
        StringBuilder origin = new StringBuilder();

        appendPart(origin, response.city());
        appendPart(origin, response.region());
        appendPart(origin, response.country());

        if (response.countryCode() != null && !response.countryCode().isBlank()) {
            if (!origin.isEmpty()) {
                origin.append(" ");
            }
            origin.append("(").append(response.countryCode()).append(")");
        }

        return origin.isEmpty() ? null : origin.toString();
    }

    private void appendPart(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(", ");
        }
        builder.append(value.trim());
    }

    private String normalizeIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return null;
        }

        String ip = ipAddress.trim();

        if (ip.startsWith("/") ) {
            ip = ip.substring(1);
        }

        int zoneIndex = ip.indexOf('%');
        if (zoneIndex >= 0) {
            ip = ip.substring(0, zoneIndex);
        }

        if (ip.startsWith("[") && ip.contains("]")) {
            ip = ip.substring(1, ip.indexOf(']'));
        } else if (ip.contains(":") && ip.chars().filter(ch -> ch == ':').count() == 1) {
            ip = ip.substring(0, ip.indexOf(':'));
        }

        return ip.isBlank() ? null : ip;
    }

    private boolean isPrivateOrLocal(String ip) {
        return "127.0.0.1".equals(ip)
                || "0.0.0.0".equals(ip)
                || "::1".equals(ip)
                || "localhost".equalsIgnoreCase(ip)
                || ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("169.254.")
                || ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")
                || ip.startsWith("fc")
                || ip.startsWith("fd")
                || ip.startsWith("fe80");
    }

    private String firstHeader(ContainerRequestContext context, String name) {
        List<String> values = context.getHeaders().get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private Map<String, List<String>> headersAsMap(MultivaluedMap<String, String> headers) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        headers.forEach((key, values) -> {
            if ("authorization".equalsIgnoreCase(key) || "cookie".equalsIgnoreCase(key)) {
                map.put(key, List.of("***"));
            } else {
                map.put(key, values);
            }
        });
        return map;
    }

    private Map<String, String> cookiesAsMap(Map<String, Cookie> cookies) {
        return cookies.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private Map<String, List<String>> multivaluedMapAsMap(
            MultivaluedMap<String, String> values
    ) {
        return new LinkedHashMap<>(values);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
