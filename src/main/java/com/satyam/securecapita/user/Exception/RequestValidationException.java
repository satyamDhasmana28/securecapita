package com.satyam.securecapita.user.Exception;

import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException{
    String message;

    public RequestValidationException(String message) {
        super(message);
        this.message = message;
    }

}
