package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.user.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service()
@Qualifier(value = "emailSmsServiceImpl")
public class EmailSmsServiceImpl implements SmsService {
    @Override
    public SmsSendingPojo sendMessage(User user, MessageTemplateEnum templateEnum, Map<String, String> placeholder) {
        return null;
    }
}
