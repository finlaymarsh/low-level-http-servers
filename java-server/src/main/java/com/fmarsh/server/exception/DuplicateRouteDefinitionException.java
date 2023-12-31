package com.fmarsh.server.exception;

public class DuplicateRouteDefinitionException extends RuntimeException {

    public DuplicateRouteDefinitionException(String message){
        super(message);
    }

    public DuplicateRouteDefinitionException(Exception cause, String message) {
        super(message, cause);
    }
}
