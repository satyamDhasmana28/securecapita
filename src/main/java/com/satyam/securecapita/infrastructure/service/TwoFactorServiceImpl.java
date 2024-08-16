package com.satyam.securecapita.infrastructure.service;

import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TwoFactorServiceImpl implements TwoFactorService{
    private final OtpTokenRepository otpTokenRepo;
    private final CustomSecurityContext context;
    @Autowired
    public TwoFactorServiceImpl(OtpTokenRepository otpTokenRepo, CustomSecurityContext context) {
        this.otpTokenRepo = otpTokenRepo;
        this.context = context;
    }

    @Override
    public boolean sendOtpFor2FA() {
        boolean isOtpSend =false;
        try{
            User user =context.getAuthenticatedUser();
            if(Objects.isNull(user.getMobileNumber()) || user.getMobileNumber().length()!=10){
                throw new ApplicationException("Mobile number is not available in database. please update it!!!");
            }
//            TODO: save the token in db and then send the message using twilio.
        } catch(Exception e){
            if(e instanceof ApplicationException){
                throw new ApplicationException(e.getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }
        return isOtpSend;
    }
}
