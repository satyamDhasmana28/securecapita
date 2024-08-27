package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.message.MessageTemplateService;
import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.infrastructure.service.TwoFactorService;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.serviceImpl.UserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApplicationConstants.BASE_PATH + "/two-factor")
public class TwoFactorController {

    private final CustomSecurityContext context;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final MessageTemplateService messageTemplateService;
    private final TwoFactorService twoFactorService;

    @Autowired
    public TwoFactorController(CustomSecurityContext context, UserRepositoryWrapper userRepositoryWrapper,
                               MessageTemplateService messageTemplateService, TwoFactorService twoFactorService) {
        this.context = context;
        this.userRepositoryWrapper = userRepositoryWrapper;
        this.messageTemplateService = messageTemplateService;
        this.twoFactorService = twoFactorService;
    }

    @RequestMapping(value = "toggle", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResponse<String>> toggleTwoFactor() {
        String message = "";
//        get authenticated user from the context
        final User user = this.context.getAuthenticatedUser();
        if (user.isTwoFactorEnabled()) {
            user.setTwoFactorEnabled(false);
            message = "Two factor disabled.";
        } else {
            message = "Two factor enabled.";
            user.setTwoFactorEnabled(true);
        }
        this.userRepositoryWrapper.saveAndFlush(user);
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null, 200, message), HttpStatus.OK);
    }

    @RequestMapping(value = "sendOtp", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResponse<String>> sendTwoFactorToken() {
        ResponseEntity<ApplicationResponse<String>> response;
        try {
            final User user = this.context.getAuthenticatedUser();
            String reqId = this.twoFactorService.sendOtpFor2FA();
            response = new ResponseEntity<>(ApplicationResponse.getSuccessResponse(reqId, HttpStatus.OK.value(), "Otp Sent to " + "******" + user.getMobileNumber().substring(6)), HttpStatus.OK);
        } catch (ApplicationException e) {
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PostMapping(value="validate/{reqId}/{enteredOtp}")
    public ResponseEntity<ApplicationResponse<String>> validate2Fa(@PathVariable("reqId") Long reqId, @PathVariable("enteredOtp") String otp){
        ResponseEntity<ApplicationResponse<String>> response;
        String message = this.twoFactorService.validateTwoFactorToken(reqId, otp);
        if(message.equalsIgnoreCase(ApplicationConstants.SUCCESS)){
            response = new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null,HttpStatus.OK.value(),"Otp verification success."),HttpStatus.OK);
        }else {
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,HttpStatus.BAD_REQUEST.value(),message),HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
