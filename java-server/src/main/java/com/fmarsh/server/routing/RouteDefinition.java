package com.fmarsh.server.routing;

import com.fmarsh.server.engine.invocation.MethodInvocationEngine;
import com.fmarsh.server.annotation.ParameterDefinitionAnnotation;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteDefinition {
    private final Object controller;
    private final Method method;
    private final List<ParameterDefinitionAnnotation<?>> parametersDefinitionsForInvocation = new ArrayList<>();

    public RouteDefinition(Object controller, Method method) {
        this.controller = controller;
        this.method = method;

        if (method == null)
            return;

        for (Parameter parameter : method.getParameters()) {
            ParameterDefinitionAnnotation<?> parameterDefinition = MethodInvocationEngine.verifyParameterHasASingleDefinitionAnnotation(parameter);
            parametersDefinitionsForInvocation.add(parameterDefinition);
        }
    }

    public HttpResponse invokeMethod(HttpRequest request) throws InvocationTargetException, IllegalAccessException {
        try {
            return (HttpResponse) method.invoke(controller, MethodInvocationEngine.sourceParametersFrom(request, parametersDefinitionsForInvocation));
        } catch (Exception e) {
            e.getCause().printStackTrace();
            throw e;
        }
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "RouteDefinition{" +
                "controller=" + controller +
                ", method=" + method +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteDefinition that = (RouteDefinition) o;
        return Objects.equals(controller, that.controller) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controller, method);
    }
}
