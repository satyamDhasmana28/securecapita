package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;

public interface UserWriteService {
    public String doUserRegisteration(UserRegistrationRequestDto requestDto);
}
