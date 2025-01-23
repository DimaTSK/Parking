package org.hofftech.parking.exception;

public class ParcelsNotFoundException extends RuntimeException {
    public ParcelsNotFoundException() {
        super("Упаковки не представлены, продолжение работы невозможно");
    }

    public ParcelsNotFoundException(String message) {
        super(message);
    }

    public ParcelsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParcelsNotFoundException(Throwable cause) {
        super(cause);
    }
}