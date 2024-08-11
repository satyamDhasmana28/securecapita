package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;

public interface UserReadService {
    boolean isUserAlreadyRegistered(String username);
    boolean isUserEmailVerified(String username);
}
