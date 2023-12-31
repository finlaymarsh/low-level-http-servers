package com.fmarsh.server;

import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;
import com.fmarsh.server.routing.RouteDefinition;
import com.fmarsh.server.routing.RouteEngine;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class HttpHandler {
    private final RouteEngine routeEngine = RouteEngine.getInstance();

    public HttpHandler() {}

    public void handleConnection(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        try (
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        ){
            Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
            if (request.isPresent()) {
                handleRequest(request.get(), bufferedWriter);
            } else {
                handleInvalidRequest(bufferedWriter);
            }
        } catch (IOException | InvocationTargetException | IllegalAccessException ignored){}
        inputStream.close();
    }

    private void handleInvalidRequest(final BufferedWriter bufferedWriter) throws IOException {
        HttpResponse notFoundResponse = new HttpResponse.Builder()
                .withStatusCode(400)
                .withEntity("Bad Request...")
                .addHeader("Content-Type", "text/plain")
                .build();
        ResponseWriter.writeResponse(bufferedWriter, notFoundResponse);
    }

    private void handleRequest(final HttpRequest request, final BufferedWriter bufferedWriter) throws IOException, InvocationTargetException, IllegalAccessException {
        Optional<RouteDefinition> routeLookupResult = routeEngine.findRouteDefinition(request.getHttpMethod(), request.getUri().getRawPath());
        if (routeLookupResult.isPresent()) {
            RouteDefinition routeDefinition = routeLookupResult.get();
            ResponseWriter.writeResponse(bufferedWriter, (HttpResponse) routeDefinition.method().invoke(routeDefinition.controller(), request));
        } else {
            ResponseWriter.writeResponse(bufferedWriter, new HttpResponse.Builder().withStatusCode(404).withEntity("Route Not Found...").build());
        }
    }
}
