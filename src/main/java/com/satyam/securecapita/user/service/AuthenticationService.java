package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.responseDto.AuthenticatedUserData;

public interface AuthenticationService {
    AuthenticatedUserData authenticate(String username, String password);
}
