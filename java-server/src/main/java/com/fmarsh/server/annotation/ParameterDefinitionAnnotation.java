package com.fmarsh.server.annotation;

import java.lang.annotation.Annotation;

/**
 * This is the main annotation on a method's parameter, it tells the custom invocation engine how to source a
 * valid argument.
 **/

public record ParameterDefinitionAnnotation<T extends Annotation>(T annotation, ParameterDefinitionType parameterType) {
}
