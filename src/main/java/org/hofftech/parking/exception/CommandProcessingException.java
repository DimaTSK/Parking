package org.hofftech.parking.exception;

public class CommandProcessingException extends RuntimeException {
    public CommandProcessingException(String message) {
        super(message);
    }

    public CommandProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}