package com.fmarsh.demo.application;

import com.fmarsh.server.model.HttpResponse;

public class ResponseBuilder {

    public HttpResponse build200Response(String entity) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity(entity)
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    public HttpResponse build400Response(String entity) {
        return new HttpResponse.Builder()
                .withStatusCode(400)
                .withEntity(entity)
                .addHeader("Content-Type", "text/plain")
                .build();
    }
}
