package com.fmarsh.server.engine.injection;

import java.lang.reflect.Constructor;
import java.util.List;

public record ClassMetaData(
        Class<?> clazz,
        String name,
        Constructor<?> constructor,
        List<Class<?>> orderedParameterTypes
){}
