package com.fmarsh.server.engine.invocation;

import com.fmarsh.server.annotation.*;
import com.fmarsh.server.exception.DuplicateParamaterDefinitionAnnotationException;
import com.fmarsh.server.exception.NoParamaterDefinitionAnnotationException;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.routing.RouteEngine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;


public class MethodInvocationEngine {
    private static final RouteEngine ROUTE_ENGINE = RouteEngine.getInstance();

    /*
        This method verifies that a Parameter has one and exactly one ParameterDefinitionAnnotation
     */
    public static ParameterDefinitionAnnotation<?> verifyParameterHasASingleDefinitionAnnotation(Parameter parameter) {
        Map<Class<?>, ParameterDefinitionType> legalAnnotationMap = getLegalAnnotationMap();
        ParameterDefinitionAnnotation<?> parameterDefinitionAnnotation = null;
        for (Annotation annotation : parameter.getAnnotations()) {
            if (legalAnnotationMap.containsKey(annotation.annotationType())) {
                if (parameterDefinitionAnnotation != null) {
                    // Todo this could be improved i.e caught in caller to give more information on location of this exception
                    throw new DuplicateParamaterDefinitionAnnotationException("Duplicate parameter definition annotations detected");
                }
               parameterDefinitionAnnotation = new ParameterDefinitionAnnotation<>(annotation, legalAnnotationMap.get(annotation.annotationType()));
            }
        }
        if (parameterDefinitionAnnotation == null) {
            // Todo this could be improved i.e caught in caller to give more information on location of this exception
            throw new NoParamaterDefinitionAnnotationException("No parameter definition annotation detected");
        }
        return parameterDefinitionAnnotation;
    }

    // Convert a list of parameter definitions and a request into an array of objects that can be used to invoke a method
    public static Object[] sourceParametersFrom(HttpRequest request, List<ParameterDefinitionAnnotation<?>> definitions) {
        List<Object> arguments = new ArrayList<>();
        for (ParameterDefinitionAnnotation<?> definition : definitions) {
            arguments.add(sourceParameterFrom(request, definition));
        }
        return arguments.toArray();
    }

    private static Object sourceParameterFrom(HttpRequest request, ParameterDefinitionAnnotation<?> definition) {
        Object object = null;
        switch (definition.parameterType()) {
            case REQUEST -> object = request;
            case HEADER -> {
                Header headerAnnotation = (Header) definition.annotation();
                object = request.getRequestHeaders().get(headerAnnotation.value());
            }
            case PATH_VARIABLE -> {
                PathVariable pathVariableAnnotation = (PathVariable) definition.annotation();
                Optional<String> result = ROUTE_ENGINE.findWildcardMatch(request.getHttpMethod(), request.getUri().getRawPath(), pathVariableAnnotation.value());
                if (result.isPresent()) {
                    object = result.get();
                }
            }
            case QUERY_PARAM -> {
                QueryParam queryParam = (QueryParam) definition.annotation();
                Optional<List<String>> param = request.queryParameter(queryParam.value());
                if (param.isPresent()) {
                    object = param.get();
                }
            }
        }
        return object;
    }

    private static Map<Class<?>, ParameterDefinitionType> getLegalAnnotationMap() {
        ParameterDefinitionType[] parameterAnnotationsArray = ParameterDefinitionType.values();
        Map<Class<?>, ParameterDefinitionType> legalAnnotationSet = new HashMap<>();
        Arrays.stream(parameterAnnotationsArray).forEach(parameterAnnotation -> legalAnnotationSet.put(parameterAnnotation.getParameterType(), parameterAnnotation));
        return legalAnnotationSet;
    }
}
