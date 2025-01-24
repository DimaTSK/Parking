package org.hofftech.parking.exception;

public class JsonWriteException extends RuntimeException {

    public JsonWriteException() {
        super();
    }

    public JsonWriteException(String message) {
        super(message);
    }

    public JsonWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonWriteException(Throwable cause) {
        super(cause);
    }
}