package org.hofftech.parking.exception;

public class TelegramBotRegistrationException extends RuntimeException {

    public TelegramBotRegistrationException(String message) {
        super(message);
    }

    public TelegramBotRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}