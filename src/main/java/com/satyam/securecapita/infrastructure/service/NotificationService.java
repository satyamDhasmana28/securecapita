package com.satyam.securecapita.infrastructure.service;

public interface NotificationService {
    boolean sendMessage(String message,String destination);
}
