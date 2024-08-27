package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TwoFactorServiceImpl implements TwoFactorService{
    private final OtpTokenRepository otpTokenRepo;
    private final CustomSecurityContext context;
    private final SmsService mobileSmsService;
    @Autowired
    public TwoFactorServiceImpl(OtpTokenRepository otpTokenRepo, CustomSecurityContext context, SmsService mobileSmsService) {
        this.otpTokenRepo = otpTokenRepo;
        this.context = context;
        this.mobileSmsService = mobileSmsService;
    }

    @Override
    public String sendOtpFor2FA() {
        SmsSendingPojo pojo = new SmsSendingPojo(ApplicationConstants.FAILURE,null);
        try{
            MessageTemplateEnum enm = MessageTemplateEnum.TWO_FACTOR;
            User user =context.getAuthenticatedUser();
            Map<String,String> hm = new HashMap<>();
//            send and save token in db
            pojo = this.mobileSmsService.sendMessage(user, enm, hm);
            if(pojo.getMessage().equalsIgnoreCase(ApplicationConstants.SUCCESS)==false || Objects.isNull(pojo.getRequestId())){
                throw new ApplicationException("Exception in sending 2FA token");
            }
            return pojo.getRequestId();
        } catch(Exception e){
            if(e instanceof ApplicationException){
                throw new ApplicationException(e.getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String validateTwoFactorToken(Long reqId, String otp) {
        return this.mobileSmsService.validateOtpToken(reqId, otp);
    }
}
