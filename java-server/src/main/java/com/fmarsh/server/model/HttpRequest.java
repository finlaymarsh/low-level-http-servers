package com.fmarsh.server.model;

import java.net.URI;
import java.util.*;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final Map<String, List<String>> requestHeaders;
    private final Map<String, List<String>> queryParameters;

    private HttpRequest(HttpMethod opCode, URI uri, Map<String, List<String>> requestHeaders, Map<String, List<String>> queryParameters) {
        this.httpMethod = opCode;
        this.uri = uri;
        this.requestHeaders = requestHeaders;
        this.queryParameters = queryParameters;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    public Optional<List<String>> queryParameter(String key) {
        return Optional.ofNullable(queryParameters.get(key));
    }

    public static class Builder {
        private HttpMethod httpMethod;
        private URI uri;
        private Map<String, List<String>> requestHeaders = Collections.emptyMap();
        private Map<String, List<String>> queryParameters = Collections.emptyMap();

        public Builder() {}

        public Builder withUri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder withHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder withHttpRequestHeaders(Map<String, List<String>> requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public Builder withQueryParameters(Map<String, List<String>> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(httpMethod, uri, requestHeaders, queryParameters);
        }
    }
}
