package org.hofftech.parking.exception;

public class InvalidLineException extends RuntimeException {
    public InvalidLineException(String message) {
        super(message);
    }
}