package com.fmarsh.demo.application;

import com.fmarsh.server.Server;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.addController(ApplicationController.class);
        server.start();
    }
}

