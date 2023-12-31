package com.fmarsh.demo.application;

import com.fmarsh.server.annotation.GetMapping;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;

public class ApplicationController {

    @GetMapping(path="/obi-wan")
    public HttpResponse helloThere(HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("Hello There!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/anakin")
    public HttpResponse highGround(HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("I have the high ground!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/healthcheck")
    public HttpResponse healthcheck(HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("oioi!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/test/{id}")
    public HttpResponse dynamicPathVariables(HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("not bad!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }
    @GetMapping(path="/test/{id}/not-bad")
    public HttpResponse dynamicPathVariables2(HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("not bad at all!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }
}

