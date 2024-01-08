package com.fmarsh.server.exception;

public class DuplicateParamaterDefinitionAnnotationException extends RuntimeException {

    public DuplicateParamaterDefinitionAnnotationException(String message){
        super(message);
    }

    public DuplicateParamaterDefinitionAnnotationException(Exception cause, String message) {
        super(message, cause);
    }
}
