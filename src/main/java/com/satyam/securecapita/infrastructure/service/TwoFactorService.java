package com.satyam.securecapita.infrastructure.service;

public interface TwoFactorService {
    String sendOtpFor2FA();

    String validateTwoFactorToken(Long reqId, String otp);
}
