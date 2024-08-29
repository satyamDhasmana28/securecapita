package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.model.User;

public interface UserReadService {
    boolean isUserAlreadyRegistered(String username);
    boolean isUserEmailVerified(String username);
    User getUserByUsername(String username);
}
