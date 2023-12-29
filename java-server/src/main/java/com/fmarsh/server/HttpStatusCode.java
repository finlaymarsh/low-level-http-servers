package com.fmarsh.server;

public class HttpStatusCode {

    public static String convertToMessage(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 203 -> "No Content";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            default -> "";
        };
    }
}
