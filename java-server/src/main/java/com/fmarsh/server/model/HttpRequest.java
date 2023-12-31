package com.fmarsh.server.model;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final Map<String, List<String>> requestHeaders;

    private HttpRequest(HttpMethod opCode, URI uri, Map<String, List<String>> requestHeaders) {
        this.httpMethod = opCode;
        this.uri = uri;
        this.requestHeaders = requestHeaders;
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

    public static class Builder {
        private HttpMethod httpMethod;
        private URI uri;
        private Map<String, List<String>> requestHeaders;

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

        public HttpRequest build() {
            return new HttpRequest(httpMethod, uri, requestHeaders);
        }
    }
}
