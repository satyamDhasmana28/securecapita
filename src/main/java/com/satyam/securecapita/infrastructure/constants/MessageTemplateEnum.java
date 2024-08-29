package com.satyam.securecapita.infrastructure.constants;

/*******************************************
 * enum defines message template for
 * different purpose
*********************************************/
public enum MessageTemplateEnum {
    TWO_FACTOR("2FA",true),
    FORGOT_PASSWORD("FORGOT PASSWORD",true);

    private String templateName;
    private boolean isOtpPurpose;

    private MessageTemplateEnum(String templateName, boolean isOtpPurpose) {
        this.templateName = templateName;
        this.isOtpPurpose = isOtpPurpose;
    }

    public String getTemplateName() {
        return templateName;
    }

    public boolean isOtpPurpose(){
        return this.isOtpPurpose;
    }
}
