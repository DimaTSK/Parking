package org.hofftech.parking.exception;

public class InsufficientTrucksException extends RuntimeException {

    public InsufficientTrucksException(String message) {
        super(message);
    }

    public InsufficientTrucksException(String message, Throwable cause) {
        super(message, cause);
    }
}