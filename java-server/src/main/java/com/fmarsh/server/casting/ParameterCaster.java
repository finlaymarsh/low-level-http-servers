package com.fmarsh.server.casting;

import java.util.List;

public class ParameterCaster {

    public static Object mapParameterToParameterType(List<String> input, ParameterCastType parameterCastType) {
        if (input.size() == 0) {
            return switch (parameterCastType) {
                case SHORT, INT, LONG, FLOAT, DOUBLE -> 0;
                case STRING -> null;
                case BOOLEAN -> false;
            };
        }

        return mapParameterToParameterType(input.get(0), parameterCastType);
    }

    public static Object mapParameterToParameterType(String input, ParameterCastType parameterCastType) {
        // Todo add better exception handling for failed parsing
        return switch (parameterCastType) {
            case SHORT -> Short.parseShort(input);
            case INT -> Integer.parseInt(input);
            case LONG -> Long.parseLong(input);
            case FLOAT -> Float.parseFloat(input);
            case DOUBLE -> Double.parseDouble(input);
            case STRING -> input;
            case BOOLEAN -> Boolean.valueOf(input);
        };
    }
}
