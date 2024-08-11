package com.satyam.securecapita.user.Exception;

import lombok.Getter;

@Getter
public class UserAlreadyRegisteredException extends Exception{
    private String message;

    public UserAlreadyRegisteredException(String message) {
        super(message);
        this.message = message;
    }
}
