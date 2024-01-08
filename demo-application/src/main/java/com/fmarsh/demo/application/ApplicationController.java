package com.fmarsh.demo.application;

import com.fmarsh.server.annotation.GetMapping;
import com.fmarsh.server.annotation.Header;
import com.fmarsh.server.annotation.PathVariable;
import com.fmarsh.server.annotation.Request;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;


public class ApplicationController {

    @GetMapping(path="/obi-wan")
    public HttpResponse helloThere() {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("Hello There!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/anakin")
    public HttpResponse highGround() {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity("I have the high ground!\n")
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/test/{id1}")
    public HttpResponse dynamicPathVariables(@PathVariable("id1") String id) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity(String.format("Dynamic path with id1 -> %s\n", id))
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/test/{id1}/{id2}")
    public HttpResponse dynamicPathVariables2(
            @PathVariable("id1") String id1,
            @PathVariable("id2") String id2
    ) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity(String.format("Dynamic path with id1 -> %s & id2 -> %s\n", id1, id2))
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/request-test")
    public HttpResponse invokeMethodWithRequest(@Request HttpRequest request) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity(String.format("Request sent with the header \"test\":\"%s\"\n", request.getRequestHeaders().get("test")))
                .addHeader("Content-Type", "text/plain")
                .build();
    }

    @GetMapping(path="/request-test-2")
    public HttpResponse invokeMethodWithHeader(@Header("test-2") Object testHeaderValue) {
        return new HttpResponse.Builder()
                .withStatusCode(200)
                .withEntity(String.format("Request sent with the header \"test-2\":\"%s\"\n", testHeaderValue))
                .addHeader("Content-Type", "text/plain")
                .build();
    }
}

