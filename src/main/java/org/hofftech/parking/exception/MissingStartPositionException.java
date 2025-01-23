package org.hofftech.parking.exception;

public class MissingStartPositionException extends RuntimeException {
    public MissingStartPositionException(String message) {
        super(message);
    }
}