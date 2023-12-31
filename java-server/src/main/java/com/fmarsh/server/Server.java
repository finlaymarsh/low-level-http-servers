package com.fmarsh.server;

import com.fmarsh.server.annotation.GetMapping;
import com.fmarsh.server.model.HttpMethod;
import com.fmarsh.server.routing.RouteDefinition;
import com.fmarsh.server.routing.RouteEngine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private final RouteEngine routeEngine = RouteEngine.getInstance();
    private final ServerSocket socket;
    private final Executor threadPool;
    private HttpHandler handler;

    public Server(int port) throws IOException {
        threadPool = Executors.newFixedThreadPool(100);
        socket = new ServerSocket(port);
    }

    public void start() throws IOException {
        handler = new HttpHandler();

        while (true) {
            Socket clientConnection = socket.accept();
            handleConnection(clientConnection);
        }
    }

    public void addController(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                try {
                    addRoute(HttpMethod.GET, method.getAnnotation(GetMapping.class).path(), new RouteDefinition(clazz.getDeclaredConstructor().newInstance(), method));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ignored) {}
            }
        }
    }

    private void handleConnection(Socket clientConnection) {
        Runnable httpRequestRunner = () -> {
            try {
                handler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream());
            } catch (IOException ignored) {}
        };
        threadPool.execute(httpRequestRunner);
    }

    private void addRoute(HttpMethod opCode, String route, RouteDefinition routeDefinition) {
        routeEngine.addRoute(opCode, route, routeDefinition);
    }
}
