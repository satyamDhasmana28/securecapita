package com.satyam.securecapita.user.Exception;

import lombok.Getter;

@Getter
public class PasswordViolationException extends RuntimeException{
    private String message;
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Password policy voilated";
    public PasswordViolationException() {
        super(DEFAULT_EXCEPTION_MESSAGE);
        this.message =DEFAULT_EXCEPTION_MESSAGE;
    }

    public PasswordViolationException(String message) {
        super(message);
        this.message = message;
    }
}
