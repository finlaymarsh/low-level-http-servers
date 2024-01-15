package com.fmarsh.server.annotation;

import com.fmarsh.server.annotation.parameter.*;

public enum ParameterDefinitionAnnotation {
    REQUEST(Request.class, false),
    REQUEST_BODY(RequestBody.class, false),
    HEADER(Header.class, true),
    PATH_VARIABLE(PathVariable.class, true),
    QUERY_PARAM(QueryParam.class, true);

    private final Class<?> annotationType;
    private final boolean needsParsing;

    ParameterDefinitionAnnotation(Class<?> annotationType, boolean needsParsing) {
        this.annotationType = annotationType;
        this.needsParsing = needsParsing;
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public boolean needsParsing() {
        return needsParsing;
    }
}
