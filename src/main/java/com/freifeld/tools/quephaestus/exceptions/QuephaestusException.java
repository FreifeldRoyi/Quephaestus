package com.freifeld.tools.quephaestus.exceptions;

public class QuephaestusException extends RuntimeException {
    public QuephaestusException(String message) {
        super(message);
    }

    public QuephaestusException(String message, Throwable cause) {
        super(message, cause);
    }
}
