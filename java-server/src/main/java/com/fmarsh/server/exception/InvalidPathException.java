package com.fmarsh.server.exception;

public class InvalidPathException extends RuntimeException {
    public InvalidPathException(String message){
        super(message);
    }

    public InvalidPathException(Exception cause, String message) {
        super(message, cause);
    }
}
