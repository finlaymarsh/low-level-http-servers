package com.fmarsh.server;

import com.fmarsh.server.annotation.GetMapping;
import com.fmarsh.server.annotation.RestController;
import com.fmarsh.server.engine.injection.DependencyInjector;
import com.fmarsh.server.model.HttpMethod;
import com.fmarsh.server.routing.RouteDefinition;
import com.fmarsh.server.routing.RouteEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final RouteEngine routeEngine = RouteEngine.getInstance();
    private final ServerSocket socket;
    private final Executor threadPool;
    private HttpHandler handler;
    private final DependencyInjector dependencyInjector;
    private final Set<Class<?>> classes = new HashSet<>();

    private Map<Class<?>, Object> beans = new HashMap<>();

    public Server(int port) throws IOException {
        printBanner();
        threadPool = Executors.newFixedThreadPool(100);
        socket = new ServerSocket(port);
        dependencyInjector = new DependencyInjector();
    }

    public void start() throws IOException {
        handler = new HttpHandler();
        while (true) {
            Socket clientConnection = socket.accept();
            handleConnection(clientConnection);
        }
    }

    public void addClassForAutoInjection(Class<?> clazz) {
        classes.add(clazz);
    }

    public void instantiateBeans() {
        beans = dependencyInjector.instantiateSingletonsFromSetOfClasses(classes);
    }

    public void catalogRoutes() {
        for (Class<?> clazz : beans.keySet()) {
            if (clazz.isAnnotationPresent(RestController.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        addRoute(HttpMethod.GET, method.getAnnotation(GetMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                }
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

    private void printBanner() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream("banner.txt")) {
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null;) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String banner = stringBuilder.toString();
        LOGGER.info(banner);
    }
}
