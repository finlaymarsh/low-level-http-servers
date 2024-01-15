package com.fmarsh.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class HttpRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    private final HttpMethod httpMethod;
    private final URI uri;
    private final Map<String, List<String>> requestHeaders;
    private final Map<String, List<String>> queryParameters;
    private final ContentType contentType;
    private final long contentLength;
    private final String body;


    private HttpRequest(HttpMethod opCode, URI uri, Map<String, List<String>> requestHeaders,
                                        Map<String, List<String>> queryParameters, String body) {
        this.httpMethod = opCode;
        this.uri = uri;
        this.requestHeaders = requestHeaders;
        this.queryParameters = queryParameters;
        this.body = body;

        // Todo remove need for duplicate header reads with HttpDecoder
        if (requestHeaders.containsKey(HttpHeader.CONTENT_TYPE.getValue())) {
            ContentType parsedContentType = ContentType.TEXT_PLAIN;
            try {
                parsedContentType = ContentType.getContentTypeFrom(requestHeaders.get(HttpHeader.CONTENT_TYPE.getValue()).get(0).trim()); // Todo Add better handling for multiple content-types
            } catch (IllegalArgumentException e) {
                LOGGER.debug("Could not parse 'Content-Type' header with value '{}'", requestHeaders.get(HttpHeader.CONTENT_TYPE.getValue()).get(0).trim());
            } finally {
                contentType = parsedContentType;
            }
        } else {
            contentType = ContentType.TEXT_PLAIN;
        }

        if (requestHeaders.containsKey(HttpHeader.CONTENT_LENGTH.getValue())) {
            contentLength = Long.parseLong(requestHeaders.get(HttpHeader.CONTENT_LENGTH.getValue()).get(0).trim()); // Todo Add better error handling when parsing int
        } else {
            contentLength = 0;
        }
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

    public ContentType getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return body;
    }

    public static class Builder {
        private HttpMethod httpMethod;
        private URI uri;
        private Map<String, List<String>> requestHeaders = Collections.emptyMap();
        private Map<String, List<String>> queryParameters = Collections.emptyMap();
        private String body = ""; // Default request body is empty

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

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }


        public HttpRequest build() {
            return new HttpRequest(httpMethod, uri, requestHeaders, queryParameters, body);
        }
    }
}
