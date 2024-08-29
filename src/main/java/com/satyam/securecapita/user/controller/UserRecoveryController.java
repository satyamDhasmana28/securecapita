package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.infrastructure.service.SmsService;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.RequestDto.ChangePasswordRequestDto;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/forgot")
@Slf4j
public class UserRecoveryController {
    private final UserReadService userReadService;
    private final SmsService mobileSmsService;

    @Autowired
    public UserRecoveryController(UserReadService userReadService, SmsService mobileSmsService) {
        this.userReadService = userReadService;
        this.mobileSmsService = mobileSmsService;
    }

    @PostMapping(value = "/password")
    public ResponseEntity<ApplicationResponse<String>> forgotPassword(@RequestParam("username") String email){
        MessageTemplateEnum enm = MessageTemplateEnum.FORGOT_PASSWORD;
        Map<String,String> hm = new HashMap<>();
        if(!this.userReadService.isUserAlreadyRegistered(email)){
            throw new ApplicationException("User not found.");
        }
        User user = this.userReadService.getUserByUsername(email);
        SmsSendingPojo smsPojo = this.mobileSmsService.sendMessage(user,enm,hm);
        if(smsPojo.getMessage().equalsIgnoreCase(ApplicationConstants.SUCCESS)==false || Objects.isNull(smsPojo.getRequestId()))
            return new ResponseEntity<>(ApplicationResponse.getFailureResponse("",400,"Issue in sending sms."), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(smsPojo.getRequestId(),200,"Otp Sent to " + "******" + user.getMobileNumber().substring(6)),HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApplicationResponse<String>> changePassword(@RequestBody ChangePasswordRequestDto dto){

    }

}
