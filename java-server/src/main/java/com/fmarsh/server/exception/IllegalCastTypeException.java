package com.fmarsh.server.exception;

public class IllegalCastTypeException extends RuntimeException {
    public IllegalCastTypeException(String message){
        super(message);
    }

    public IllegalCastTypeException(Exception cause, String message) {
        super(message, cause);
    }
}
