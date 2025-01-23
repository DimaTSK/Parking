package org.hofftech.parking.exception;

public class ParcelLoadingException extends RuntimeException {
    public ParcelLoadingException(String message) {
        super(message);
    }

    public ParcelLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}