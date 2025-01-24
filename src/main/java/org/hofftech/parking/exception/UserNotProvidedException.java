package org.hofftech.parking.exception;

public class UserNotProvidedException extends RuntimeException {

    public UserNotProvidedException(String message) {
        super(message);
    }
}