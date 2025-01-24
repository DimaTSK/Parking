package org.hofftech.parking.exception;

public class EmptyCommandArgumentException extends RuntimeException {
    public EmptyCommandArgumentException() {
        super("Первый аргумент команды пуст");
    }

    public EmptyCommandArgumentException(String message) {
        super(message);
    }

    public EmptyCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyCommandArgumentException(Throwable cause) {
        super(cause);
    }
}