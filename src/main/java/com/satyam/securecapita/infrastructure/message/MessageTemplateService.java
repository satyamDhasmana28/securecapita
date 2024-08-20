package com.satyam.securecapita.infrastructure.message;

import java.util.Map;

public interface MessageTemplateService {
    String getProcessMessage(String templateName, Map<String ,String> placeHolders);
}
