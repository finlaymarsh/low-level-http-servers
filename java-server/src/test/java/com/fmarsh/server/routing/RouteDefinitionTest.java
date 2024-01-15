package com.fmarsh.server.routing;

import com.fmarsh.server.annotation.mapping.GetMapping;
import com.fmarsh.server.annotation.parameter.Header;
import com.fmarsh.server.annotation.parameter.Request;
import com.fmarsh.server.exception.DuplicateParamaterDefinitionAnnotationException;
import com.fmarsh.server.exception.NoParamaterDefinitionAnnotationException;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteDefinitionTest {

    @Test
    void testMethodWithSingleParameterAndNoAnnotation() throws Exception {
        ExampleController exampleController = new ExampleController();
        Method method = getMethodOnExampleControllerWithSignature("parseMethodWithParameterAndNoAnnotation", List.of(HttpRequest.class));
        NoParamaterDefinitionAnnotationException exception = assertThrows(NoParamaterDefinitionAnnotationException.class, () -> new RouteDefinition(exampleController, method));
        assertEquals("No parameter definition annotation detected", exception.getMessage());
    }

    @Test
    void testMethodWithSingeParameterAndAnnotation() throws Exception {
        ExampleController exampleController = new ExampleController();
        Method method = getMethodOnExampleControllerWithSignature("parseMethodWithSingleParameterAndAnnotation", List.of(HttpRequest.class));
        assertDoesNotThrow(() -> new RouteDefinition(exampleController, method));
    }

    @Test
    void testMethodWithSingeParameterAndDuplicateAnnotations() throws Exception {
        ExampleController exampleController = new ExampleController();
        Method method = getMethodOnExampleControllerWithSignature("parseMethodWithSingleParameterAndDuplicateAnnotations", List.of(String.class));
        DuplicateParamaterDefinitionAnnotationException exception = assertThrows(DuplicateParamaterDefinitionAnnotationException.class, () -> new RouteDefinition(exampleController, method));
        assertEquals("Duplicate parameter definition annotations detected", exception.getMessage());
    }

    private Method getMethodOnExampleControllerWithSignature(String methodName, List<Class<?>> parameters) throws NoSuchMethodException {
        return ExampleController.class.getMethod(methodName, parameters.toArray(Class<?>[]::new));
    }

    public static class ExampleController {

        @GetMapping(path="/test")
        public HttpResponse parseMethodWithParameterAndNoAnnotation(HttpRequest request) {
            return new HttpResponse.Builder().build();
        }

        @GetMapping(path="/test")
        public HttpResponse parseMethodWithSingleParameterAndAnnotation(@Request HttpRequest request) {
            return new HttpResponse.Builder().build();
        }

        @GetMapping(path="/test")
        public HttpResponse parseMethodWithSingleParameterAndDuplicateAnnotations(@Request @Header("test") String header) {
            return new HttpResponse.Builder().build();
        }
    }
}
