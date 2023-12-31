package com.fmarsh.server.routing;

import java.lang.reflect.Method;

public record RouteDefinition(Object controller, Method method) {
}
