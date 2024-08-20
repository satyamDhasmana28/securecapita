package com.satyam.securecapita.infrastructure.message;

import com.satyam.securecapita.user.Exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService{
    private final MessageTemplateRepository messageTemplateRepository;

    @Autowired
    public MessageTemplateServiceImpl(MessageTemplateRepository messageTemplateRepository) {
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @Override
    public String getProcessMessage(String templateName, Map<String, String> placeHolders) {
        MessageTemplate messageTemplate =this.messageTemplateRepository.findByTemplateNameIgnoreCase(templateName).filter((obj)->obj.isAuthorised()).
                orElseThrow(() -> new ApplicationException(templateName + " is either not available or not authorized."));

        String msgTemp = messageTemplate.getMessageTemplate();

        for(Map.Entry entry:placeHolders)

    }
}
