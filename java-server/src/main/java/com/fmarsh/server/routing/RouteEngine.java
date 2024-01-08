package com.fmarsh.server.routing;

import com.fmarsh.server.exception.DuplicateRouteDefinitionException;
import com.fmarsh.server.exception.InvalidPathException;
import com.fmarsh.server.model.HttpMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class RouteEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteEngine.class);
    private static final RouteEngine INSTANCE = new RouteEngine();
    private final Map<HttpMethod, GenesisNode> genesisNodeIndex;

    private RouteEngine() {
        genesisNodeIndex = new HashMap<>();
        for (HttpMethod method : HttpMethod.values()) {
            genesisNodeIndex.put(method, new GenesisNode(method));
        }
    }

    public static RouteEngine getInstance() {
        return INSTANCE;
    }

    public void flushRoutes() {
        for (GenesisNode node : genesisNodeIndex.values()) {
            node.getChildren().clear();
        }
    }

    public Optional<RouteDefinition> findRouteDefinition(HttpMethod httpMethod, String rawPath){
        GenesisNode genesis = genesisNodeIndex.get(httpMethod);
        String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
        String[] splitPath = path.split("/");
        Node currentNode = genesis;

        for (String s : splitPath) {
            if (currentNode.containsChildWithPath(s)) {
                currentNode = currentNode.getChildWithPath(s);
                continue;
            }
            if (currentNode.getChildren().size() == 1) {
                Optional<Node> possibleWildcard = currentNode.getChildren().values().stream().findFirst();
                if (possibleWildcard.isPresent() && possibleWildcard.get().isWildcard()) {
                    currentNode = currentNode.getChildren().values().iterator().next();
                    continue;
                }
            }
            return Optional.empty();
        }
        if (!currentNode.isLeaf()) {
            return Optional.empty();
        }
        return Optional.ofNullable(currentNode.getRouteDefinition());
    }

    // Todo restrict routing so that a wildcard value can only appear once
    // i.e foo/{id1}/{id2} is still legal but foo/{id}/{id} is not
    public Optional<String> findWildcardMatch(HttpMethod httpMethod, String rawPath, String wildCardValue){
        GenesisNode genesis = genesisNodeIndex.get(httpMethod);
        String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
        String[] splitPath = path.split("/");
        Node currentNode = genesis;

        for (String s : splitPath) {
            if (currentNode.containsChildWithPath(s)) {
                currentNode = currentNode.getChildWithPath(s);
                continue;
            }
            if (currentNode.getChildren().size() == 1) {
                Optional<Node> possibleWildcard = currentNode.getChildren().values().stream().findFirst();
                if (possibleWildcard.isPresent() && possibleWildcard.get().isWildcard()) {
                    Node wildCardNode = currentNode.getChildren().values().iterator().next();
                    if (wildCardNode.getPath().equals(String.format("{%s}", wildCardValue))) {
                        return Optional.of(s);
                    }
                    currentNode = wildCardNode;
                    continue;
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Node addRoute(HttpMethod httpMethod, String path, RouteDefinition routeDefinition) {
        Node leaf = createRoute(genesisNodeIndex.get(httpMethod), path, routeDefinition);
        LOGGER.info("Registered route: [{}]", RoutingHelper.getPathFrom(leaf));
        return leaf;
    }

    private Node createRoute(GenesisNode genesis, String rawPath, RouteDefinition routeDefinition) {
        String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
        String[] splitPath = path.split("/");
        Node currentNode = genesis;

        for (int index = 0; index < splitPath.length; index++) {
            if (index == splitPath.length - 1) {
                currentNode = addNode(currentNode, splitPath[index], routeDefinition);
            } else {
                currentNode = addNode(currentNode, splitPath[index], null);
            }
        }
        return currentNode;
    }

    private Node addNode(Node currentNode, final String path, RouteDefinition routeDefinition){
        validatePath(path);

        if (currentNode.containsChildWithPath(path)) {
            // Leaf cannot have conflicting path
            if (routeDefinition != null) {
                throw new DuplicateRouteDefinitionException(
                    String.format("The route: [%s/%s] is defined multiple times.", RoutingHelper.getPathFrom(currentNode), path)
                );
            }
            return currentNode.getChildWithPath(path);
        }

        // If wildcard exists on leaf then no more routes can be registered as a leaf
        if (currentNode.getChildren().values().stream().anyMatch(Node::isWildcard)) {
            throw new DuplicateRouteDefinitionException(String.format("Path collision detected: [%s/*here*].", RoutingHelper.getPathFrom(currentNode)));
        }

        if (isWildcardPath(path)) {
            // Wildcard must be only route registered on leaf
            if (currentNode.getChildren().values().size() > 0) {
                throw new DuplicateRouteDefinitionException(String.format("Path collision detected: [%s/*here*].", RoutingHelper.getPathFrom(currentNode)));
            }
            currentNode.addChild(new RouteNode(currentNode, path, true, routeDefinition));
        } else {
            currentNode.addChild(new RouteNode(currentNode, path, false, routeDefinition));
        }

        return currentNode.getChildWithPath(path);
    }

    private boolean isWildcardPath(final String path) {
        return path.startsWith("{") && path.endsWith("}");
    }

    private void validatePath(final String path) {
        if (path.length() == 0) {
            throw new InvalidPathException("Empty path");
        }

        if (path.substring(1).chars().filter(c -> c == '{').count() > 0) {
            throw new InvalidPathException("Invalid path");
        }

        if (path.substring(0, path.length() - 1).chars().filter(c -> c == '}').count() > 0) {
            throw new InvalidPathException("Invalid path");
        }

        if (path.startsWith("{") && !path.endsWith("}")) {
            throw new InvalidPathException("Invalid path");
        }

        if (!path.startsWith("{") && path.endsWith("}")) {
            throw new InvalidPathException("Invalid path");
        }
    }
}
