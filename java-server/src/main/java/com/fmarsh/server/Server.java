package com.fmarsh.server;

import com.fmarsh.server.annotation.clazz.RestController;
import com.fmarsh.server.annotation.mapping.*;
import com.fmarsh.server.engine.annotation.AnnotationDetailsEngine;
import com.fmarsh.server.engine.injection.DependencyInjector;
import com.fmarsh.server.model.HttpMethod;
import com.fmarsh.server.routing.RouteDefinition;
import com.fmarsh.server.routing.RouteEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final Map<Class<?>, Set<Class<?>>> ANNOTATION_LOOKUP_INDEX = AnnotationDetailsEngine.constructAnnotationLookupIndex();

    private final RouteEngine routeEngine = RouteEngine.getInstance();
    private final ServerSocket socket;
    private final Executor threadPool;
    private final DependencyInjector dependencyInjector;
    private final Set<Class<?>> classes = new HashSet<>();
    private Map<Class<?>, Object> beans = new HashMap<>();

    public Server(int port) throws IOException, URISyntaxException {
        String dir = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        printBanner();

        LOGGER.info("current dir = " + dir);
        LOGGER.info("{}", ANNOTATION_LOOKUP_INDEX);

        threadPool = Executors.newFixedThreadPool(100);
        socket = new ServerSocket(port);
        dependencyInjector = new DependencyInjector();
    }

    public void start() throws IOException {
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
            // Todo use a lookup table here instead
            if (clazz.isAnnotationPresent(RestController.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        addRoute(HttpMethod.GET, method.getAnnotation(GetMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                    if (method.isAnnotationPresent(PostMapping.class)) {
                        addRoute(HttpMethod.POST, method.getAnnotation(PostMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                    if (method.isAnnotationPresent(DeleteMapping.class)) {
                        addRoute(HttpMethod.DELETE, method.getAnnotation(DeleteMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                    if (method.isAnnotationPresent(PutMapping.class)) {
                        addRoute(HttpMethod.PUT, method.getAnnotation(PutMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                    if (method.isAnnotationPresent(PatchMapping.class)) {
                        addRoute(HttpMethod.PATCH, method.getAnnotation(PatchMapping.class).path(), new RouteDefinition(beans.get(clazz), method));
                    }
                }
            }
        }
    }

    private void handleConnection(Socket clientConnection) {
        Runnable httpRequestRunner = () -> {
            try {
                HttpHandler httpHandler = new HttpHandler();
                httpHandler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream());
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
