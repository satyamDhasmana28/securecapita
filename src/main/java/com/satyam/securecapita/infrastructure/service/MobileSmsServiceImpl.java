package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.constants.TwilioConfig;
import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.OtpTokenRepository;
import com.twilio.Twilio;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@Qualifier("mobileSmsServiceImpl")
public class MobileSmsServiceImpl implements NotificationService{

    private final TwilioConfig twilioConfig;
    private final OtpTokenRepository otpRepo;
    private final CustomSecurityContext context;
    @Autowired
    public MobileSmsServiceImpl(TwilioConfig twilioConfig, OtpTokenRepository otpRepo, CustomSecurityContext context) {
        this.twilioConfig = twilioConfig;
        this.otpRepo = otpRepo;
        this.context = context;
    }

    @Override
    public boolean sendMessage(String message, String destination) {
        boolean isOtpSend = false;
        try {
//          send message to customer customer
            final User user = this.context.getAuthenticatedUser();
            String mobNo = user.getMobileNumber();
            if(Objects.isNull(mobNo) || mobNo.length()!=10){
                throw new ApplicationException("mobile number not available");
            }
            Twilio.init(this.twilioConfig.getAccountSid(),this.twilioConfig.getAuthToken());
            Message.creator(new PhoneNumber(destination),new PhoneNumber(this.twilioConfig.getPhoneNumber()),message).create();
            isOtpSend = true;
        } catch (Exception e){
            throw new ApplicationException(e.getMessage());
        }
        return isOtpSend;
    }
}
