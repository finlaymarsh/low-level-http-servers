package com.fmarsh.server.routing;

public class RoutingHelper {

    public static String getPathFrom(Node node) {
        StringBuilder path = new StringBuilder(node.getPath());
        Node currentNode = node;

        while (currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
            if (currentNode.getPath().equals("/")){
                continue;
            }
            path.insert(0, String.format("%s/", currentNode.getPath()));
        }

        if (currentNode instanceof GenesisNode genesisNode) {
            path.insert(0, String.format("%s %s", genesisNode.getHttpMethod().name(), genesisNode.getPath()));
        }

        return path.toString();
    }
}
