package com.fmarsh.server;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

public class HttpHandler {
    private final Map<String, RouteDefinition> routes;

    public HttpHandler(final Map<String, RouteDefinition> routes) {
        this.routes = routes;
    }

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
        final String routeKey = request.getHttpMethod().name().concat(request.getUri().getRawPath());
        if (routes.containsKey(routeKey)) {
            RouteDefinition routeDefinition = routes.get(routeKey);
            ResponseWriter.writeResponse(bufferedWriter, (HttpResponse) routeDefinition.method().invoke(routeDefinition.clazz(), request));
        } else {
            ResponseWriter.writeResponse(bufferedWriter, new HttpResponse.Builder().withStatusCode(404).withEntity("Route Not Found...").build());
        }
    }
}
