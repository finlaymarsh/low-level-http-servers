package com.fmarsh.demo.application;

import com.fmarsh.server.Server;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Server server = new Server(8080);

        // I invision a world where this can all be done in the background
        server.addClassForAutoInjection(DemoController.class);
        server.addClassForAutoInjection(DemoService.class);
        server.addClassForAutoInjection(ResponseBuilder.class);
        server.instantiateBeans();
        server.catalogRoutes();

        server.start();
    }
}

