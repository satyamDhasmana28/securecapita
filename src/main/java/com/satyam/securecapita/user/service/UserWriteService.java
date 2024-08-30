package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.model.User;

public interface UserWriteService {
    public User doUserRegisteration(UserRegistrationRequestDto requestDto) throws UserAlreadyRegisteredException;

    String verifyEmailUsingToken(String token);

    void saveUser(User user);
}
