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
    public String getProcessMessage(String templateMessage, Map<String, String> placeHolders) {
        String processedMsg=null;
        for(Map.Entry<String,String> entry:placeHolders.entrySet()){
            processedMsg = templateMessage.replace("{"+entry.getKey()+"}",entry.getValue());
        }
        return processedMsg;
    }
}
