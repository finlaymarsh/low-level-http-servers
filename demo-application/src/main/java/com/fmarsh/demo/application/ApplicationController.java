package com.fmarsh.demo.application;

import com.fmarsh.server.GetMapping;
import com.fmarsh.server.HttpRequest;
import com.fmarsh.server.HttpResponse;

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
                .withEntity("Roger roger!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }
}

