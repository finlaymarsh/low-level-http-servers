package com.fmarsh.server;

public class AppController {

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
}
