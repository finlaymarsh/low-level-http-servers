package com.fmarsh.server.routing;

import com.fmarsh.server.exception.DuplicateRouteDefinitionException;

import java.util.HashMap;
import java.util.Map;

public class RouteNode implements Node {
    private final Node parent;
    private final String path;
    private final Map<String, Node> children;
    private final boolean wildcard;
    private RouteDefinition routeDefinition;

    public RouteNode(Node parent, String path, boolean wildcard) {
        this.parent = parent;
        this.path = path;
        this.wildcard = wildcard;
        this.children = new HashMap<>();
        this.routeDefinition = null;
    }

    public RouteNode(Node parent, String path, boolean wildcard, RouteDefinition routeDefinition) {
        this.parent = parent;
        this.path = path;
        this.wildcard = wildcard;
        this.children = new HashMap<>();
        this.routeDefinition = routeDefinition;
    }

    @Override
    public Map<String, Node> getChildren() {
        return children;
    }

    @Override
    public void addChild(Node routeNode) {
        if (children.containsKey(routeNode.getPath())) {
            throw new DuplicateRouteDefinitionException("A parent node cannot contain two children with the same value for: [path]");
        }
        children.put(routeNode.getPath(), routeNode);
    }

    @Override
    public Node getChildWithPath(String path) {
        return children.get(path);
    }

    @Override
    public boolean containsChildWithPath(String path) {
        return children.containsKey(path);
    }

    @Override
    public RouteDefinition getRouteDefinition() {
        return routeDefinition;
    }

    @Override
    public void setRouteDefinition(RouteDefinition routeDefinition) {
        this.routeDefinition = routeDefinition;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public boolean isWildcard() {
        return wildcard;
    }

    @Override
    public boolean isLeaf() {
        return routeDefinition != null;
    }


    @Override
    public String getPath() {
        return path;
    }
    
}
