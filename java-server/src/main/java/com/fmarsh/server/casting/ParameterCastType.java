package com.fmarsh.server.casting;

import com.fmarsh.server.exception.IllegalCastTypeException;

import java.lang.reflect.Parameter;
import java.util.Map;

public enum ParameterCastType {
    SHORT(short.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class),
    STRING(String.class),
    BOOLEAN(boolean.class);

    private static final Map<Class<?>, ParameterCastType> LEGAL_TYPES = Map.of(
            short.class, SHORT,
            int.class, INT,
            long.class, LONG,
            float.class, FLOAT,
            double.class, DOUBLE,
            String.class, STRING,
            boolean.class, BOOLEAN
    );

    private final Class<?> clazz;

    ParameterCastType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static ParameterCastType isParameterTypeALegalCast(Parameter parameter) {
        if (!LEGAL_TYPES.containsKey(parameter.getType())) {
            throw new IllegalCastTypeException(String.format("Cannot cast parameter to type: %s", parameter.getType().getSimpleName()));
        }

        return LEGAL_TYPES.get(parameter.getType());
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
