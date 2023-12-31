package com.fmarsh.server.routing;

import java.util.Map;

public interface Node {
    Node getParent();
    boolean isWildcard();
    boolean isLeaf();
    String getPath();
    Map<String, Node> getChildren();
    void addChild(Node node);
    Node getChildWithPath(String path);
    boolean containsChildWithPath(String path);
    RouteDefinition getRouteDefinition();
}
