package com.satyam.securecapita.user.Exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException{

    @Getter
    private String message;

    public ApplicationException(String message) {
        super(message);
        this.message=message;
    }
}
