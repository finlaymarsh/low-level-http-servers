package com.fmarsh.server.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
    private final Map<String, List<String>> responseHeaders;
    private final int statusCode;
    private final Optional<Object> entity;

    /**
     * Headers should contain the following:
     * Date: < date >
     * Server: < my server >
     * Content-Type: text/plain, application/json etc...
     * Content-Length: size of payload
     */
    private HttpResponse(Map<String, List<String>> responseHeaders, int statusCode, Optional<Object> entity) {
        this.responseHeaders = responseHeaders;
        this.statusCode = statusCode;
        this.entity = entity;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<Object> getEntity() {
        return entity;
    }

    public static class Builder {
        private Map<String, List<String>> responseHeaders;
        private int statusCode;
        private Optional<Object> entity;

        public Builder() {
            // Create default headers - server etc
            responseHeaders = new HashMap<>();
            responseHeaders.put("Server", List.of("MyServer"));
            responseHeaders.put("Date", List.of(DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC))));
            responseHeaders.put("Connection", List.of("close"));
            entity = Optional.empty();
        }

        public Builder addHeader(String name, String value) {
            responseHeaders.put(name, List.of(value));
            return this;
        }

        public Builder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withEntity(Object entity) {
            this.entity = Optional.ofNullable(entity);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(responseHeaders, statusCode, entity);
        }
    }
}
