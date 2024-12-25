package org.hofftech.parking.exception;

public class InvalidJsonStructureException extends RuntimeException {
    public InvalidJsonStructureException(String message) {
        super(message);
    }
}