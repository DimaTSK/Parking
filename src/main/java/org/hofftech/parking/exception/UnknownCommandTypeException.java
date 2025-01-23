package org.hofftech.parking.exception;

public class UnknownCommandTypeException extends IllegalArgumentException {
    public UnknownCommandTypeException(String command) {
        super("Неизвестная команда: " + command);
    }

    public UnknownCommandTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}