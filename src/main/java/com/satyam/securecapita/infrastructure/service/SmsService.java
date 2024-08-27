package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.user.model.User;

import java.util.Map;

public interface SmsService {
    SmsSendingPojo sendMessage(User user, MessageTemplateEnum templateEnum, Map<String,String> placeholder);

    String validateOtpToken(Long reqId, String otp);
}
