package com.fmarsh.server.engine.invocation;

import com.fmarsh.server.annotation.*;
import com.fmarsh.server.annotation.parameter.Header;
import com.fmarsh.server.annotation.parameter.PathVariable;
import com.fmarsh.server.annotation.parameter.QueryParam;
import com.fmarsh.server.casting.ParameterCastType;
import com.fmarsh.server.exception.DuplicateParamaterDefinitionAnnotationException;
import com.fmarsh.server.exception.NoParamaterDefinitionAnnotationException;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.routing.RouteEngine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;

import static com.fmarsh.server.casting.ParameterCaster.mapParameterToParameterType;


public class MethodInvocationEngine {
    private static final RouteEngine ROUTE_ENGINE = RouteEngine.getInstance();

    /*
        This method verifies that a Parameter has one and exactly one ParameterMetadata
     */
    public static ParameterMetadata<?> verifyParameterHasASingleDefinitionAnnotation(Parameter parameter) {
        Map<Class<?>, ParameterDefinitionAnnotation> legalAnnotationMap = getLegalAnnotationMap();
        ParameterMetadata<?> parameterMetadata = null;
        for (Annotation annotation : parameter.getAnnotations()) {
            if (legalAnnotationMap.containsKey(annotation.annotationType())) {
                if (parameterMetadata != null) {
                    // Todo this could be improved i.e caught in caller to give more information on location of this exception
                    throw new DuplicateParamaterDefinitionAnnotationException("Duplicate parameter definition annotations detected");
                }
                ParameterDefinitionAnnotation parameterDefinitionAnnotation = legalAnnotationMap.get(annotation.annotationType());

                // Not all parameter types need to be parsed, i.e a HttpRequest can be injected without the need for casting
                ParameterCastType parameterCastType = null;
                if (parameterDefinitionAnnotation.needsParsing()) {
                    parameterCastType = ParameterCastType.isParameterTypeALegalCast(parameter);
                }

                parameterMetadata = new ParameterMetadata<>(annotation, parameterCastType, parameterDefinitionAnnotation);
            }
        }
        if (parameterMetadata == null) {
            // Todo this could be improved i.e caught in caller to give more information on location of this exception
            throw new NoParamaterDefinitionAnnotationException("No parameter definition annotation detected");
        }
        return parameterMetadata;
    }

    // Convert a list of parameter definitions and a request into an array of objects that can be used to invoke a method
    public static Object[] sourceParametersFrom(HttpRequest request, List<ParameterMetadata<?>> definitions) {
        List<Object> arguments = new ArrayList<>();
        for (ParameterMetadata<?> definition : definitions) {
            arguments.add(sourceParameterFrom(request, definition));
        }
        return arguments.toArray();
    }

    private static Object sourceParameterFrom(HttpRequest request, ParameterMetadata<?> parameterMetadata) {
        Object object = null;
        switch (parameterMetadata.parameterDefinitionAnnotation()) {
            case REQUEST -> object = request;
            case REQUEST_BODY -> object = request.getBody();
            case HEADER -> {
                Header headerAnnotation = (Header) parameterMetadata.annotation();
                object = mapParameterToParameterType(request.getRequestHeaders().getOrDefault(headerAnnotation.value(), Collections.emptyList()), parameterMetadata.parameterCastType());
            }
            case PATH_VARIABLE -> {
                PathVariable pathVariableAnnotation = (PathVariable) parameterMetadata.annotation();
                Optional<String> result = ROUTE_ENGINE.findWildcardMatch(request.getHttpMethod(), request.getUri().getRawPath(), pathVariableAnnotation.value());
                object = mapParameterToParameterType(result.orElse(null), parameterMetadata.parameterCastType());
            }
            case QUERY_PARAM -> {
                QueryParam queryParam = (QueryParam) parameterMetadata.annotation();
                Optional<List<String>> param = request.queryParameter(queryParam.value());
                object = mapParameterToParameterType(param.orElse(Collections.emptyList()), parameterMetadata.parameterCastType());
            }
        }
        return object;
    }

    private static Map<Class<?>, ParameterDefinitionAnnotation> getLegalAnnotationMap() {
        ParameterDefinitionAnnotation[] parameterAnnotationsArray = ParameterDefinitionAnnotation.values();
        Map<Class<?>, ParameterDefinitionAnnotation> legalAnnotationSet = new HashMap<>();
        Arrays.stream(parameterAnnotationsArray).forEach(parameterAnnotation -> legalAnnotationSet.put(parameterAnnotation.getAnnotationType(), parameterAnnotation));
        return legalAnnotationSet;
    }
}
