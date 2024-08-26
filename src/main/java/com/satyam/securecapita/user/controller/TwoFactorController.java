package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.constants.ApplicationUtil;
import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.infrastructure.message.MessageTemplateService;
import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.infrastructure.service.SmsService;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.serviceImpl.UserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/two-factor")
public class TwoFactorController {

    private final CustomSecurityContext context;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final SmsService mobileSmsService;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public TwoFactorController(CustomSecurityContext context, UserRepositoryWrapper userRepositoryWrapper,
                               @Qualifier("mobileSmsServiceImpl") SmsService mobileSmsService, MessageTemplateService messageTemplateService) {
        this.context = context;
        this.userRepositoryWrapper = userRepositoryWrapper;
        this.mobileSmsService = mobileSmsService;
        this.messageTemplateService = messageTemplateService;
    }

    @RequestMapping(value = "toggle", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResponse<String>> toggleTwoFactor(){
        String message = "";
//        get authenticated user from the context
        final User user = this.context.getAuthenticatedUser();
        if(user.isTwoFactorEnabled()){
            user.setTwoFactorEnabled(false);
            message = "Two factor disabled.";
        } else{
            message = "Two factor enabled.";
            user.setTwoFactorEnabled(true);
        }
        this.userRepositoryWrapper.saveAndFlush(user);
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null,200,message), HttpStatus.OK);
    }

    @RequestMapping(value = "sendOtp", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResponse<String>> sendTwoFactorToken(){
        ResponseEntity<ApplicationResponse<String>> response;
        try{
            final User user = this.context.getAuthenticatedUser();
            Map<String,String> hm = new HashMap<>();
            SmsSendingPojo obj =this.mobileSmsService.sendMessage(user, MessageTemplateEnum.TWO_FACTOR,hm);
            if(!obj.getMessage().equalsIgnoreCase(ApplicationConstants.SUCCESS)){
                throw new ApplicationException(obj.getMessage());
            }
            response = new ResponseEntity<>(ApplicationResponse.getSuccessResponse(obj.getRequestId(),HttpStatus.OK.value(),"Otp Sent to "+"******"+user.getMobileNumber().substring(6)),HttpStatus.OK);
        } catch(ApplicationException e){
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
