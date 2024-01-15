package com.fmarsh.server.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ContentType {
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain"),
    FORM("application/x-www-form-urlencoded");

    private static final Map<String, ContentType> LOOKUP = Arrays.stream(ContentType.values()).collect(Collectors.toMap(ContentType::getValue, (identity) -> identity));

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType getContentTypeFrom(String contentType) {
        if (LOOKUP.containsKey(contentType)) {
            return LOOKUP.get(contentType);
        }
        throw new IllegalArgumentException(String.format("{ %s } is not a valid ContentType", contentType));
    }
}
