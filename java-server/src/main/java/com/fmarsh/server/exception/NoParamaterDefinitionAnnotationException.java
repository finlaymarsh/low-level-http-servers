package com.fmarsh.server.exception;

public class NoParamaterDefinitionAnnotationException extends RuntimeException {

    public NoParamaterDefinitionAnnotationException(String message){
        super(message);
    }

    public NoParamaterDefinitionAnnotationException(Exception cause, String message) {
        super(message, cause);
    }
}
