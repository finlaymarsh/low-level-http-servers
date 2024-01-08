package com.fmarsh.server.annotation;

public enum ParameterDefinitionType {
    REQUEST(Request.class),
    HEADER(Header.class),
    PATH_VARIABLE(PathVariable.class);

    private final Class<?> parameterType;

    ParameterDefinitionType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }
}
