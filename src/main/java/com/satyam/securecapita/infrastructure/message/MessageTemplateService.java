package com.satyam.securecapita.infrastructure.message;

import java.util.Map;

public interface MessageTemplateService {
    String getProcessMessage(String templateMessage, Map<String, String> placeHolders);
}
