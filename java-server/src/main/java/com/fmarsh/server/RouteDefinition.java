package com.fmarsh.server;

import java.lang.reflect.Method;

public record RouteDefinition(Object clazz, Method method) {
}
