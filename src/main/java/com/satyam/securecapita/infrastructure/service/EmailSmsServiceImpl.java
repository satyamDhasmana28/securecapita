package com.satyam.securecapita.infrastructure.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service()
@Qualifier(value = "emailSmsServiceImpl")
public class EmailSmsServiceImpl implements NotificationService {
    @Override
    public boolean sendMessage(String message, String destination) {
        return false;
    }
}
