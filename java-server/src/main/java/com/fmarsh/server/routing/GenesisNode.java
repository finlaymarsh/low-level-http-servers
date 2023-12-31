package com.fmarsh.server.routing;

import com.fmarsh.server.model.HttpMethod;

public class GenesisNode extends RouteNode {
    private final HttpMethod httpMethod;

    public GenesisNode(HttpMethod httpMethod) {
        super(null, "/", false);
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
