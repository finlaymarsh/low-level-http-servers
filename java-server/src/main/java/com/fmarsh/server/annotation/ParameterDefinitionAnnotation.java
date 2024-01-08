package com.fmarsh.server.annotation;

import java.lang.annotation.Annotation;

/**

 This is the main annotation on a method's parameter, it tells the custom invocation engine how to source a
 valid argument.

**/

public class ParameterDefinitionAnnotation<T extends Annotation> {
    private final T annotation;
    private final ParameterDefinitionType parameterType;

    ParameterDefinitionAnnotation(T annotation, ParameterDefinitionType parameterType) {
        this.annotation = annotation;
        this.parameterType = parameterType;
    }

    public T getAnnotation() {
        return annotation;
    }

    public ParameterDefinitionType getParameterType() {
        return parameterType;
    }
}
