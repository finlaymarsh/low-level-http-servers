package com.fmarsh.server.engine.invocation;

import com.fmarsh.server.casting.ParameterCastType;
import com.fmarsh.server.engine.invocation.ParameterDefinitionAnnotation;

import java.lang.annotation.Annotation;

/**
 * This is the main annotation on a method's parameter, it tells the custom invocation engine how to source a
 * valid argument.
 **/

public record ParameterMetadata<T extends Annotation>(T annotation, ParameterCastType parameterCastType, ParameterDefinitionAnnotation parameterDefinitionAnnotation) {}
