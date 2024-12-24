package org.hofftech.parking.exception;

public class MissingParcelPositionException extends RuntimeException {
    public MissingParcelPositionException(String message) {
        super(message);
    }
}