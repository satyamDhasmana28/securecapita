package com.satyam.securecapita.infrastructure.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("mobileSmsServiceImpl")
public class MobileSmsServiceImpl implements NotificationService{
    @Override
    public boolean sendMessage(String message, String destination) {
        return false;
    }
}
