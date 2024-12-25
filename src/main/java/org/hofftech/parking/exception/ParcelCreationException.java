package org.hofftech.parking.exception;

public class ParcelCreationException extends RuntimeException {
    public ParcelCreationException(String message) {
        super(message);
    }

    public ParcelCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}