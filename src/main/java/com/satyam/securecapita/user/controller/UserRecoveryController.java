package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.constants.MessageTemplateEnum;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.data.SmsSendingPojo;
import com.satyam.securecapita.infrastructure.service.SmsService;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.RequestDto.ChangePasswordRequestDto;
import com.satyam.securecapita.user.RequestDto.OtpValidationRequestDto;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserReadService;
import com.satyam.securecapita.user.service.UserWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/forgot")
@Slf4j
public class UserRecoveryController {
    private final UserReadService userReadService;
    private final SmsService mobileSmsService;
    private final UserWriteService userWriteService;

    @Autowired
    public UserRecoveryController(UserReadService userReadService, SmsService mobileSmsService, UserWriteService userWriteService) {
        this.userReadService = userReadService;
        this.mobileSmsService = mobileSmsService;
        this.userWriteService = userWriteService;
    }

    @PostMapping(value = "/password")
    public ResponseEntity<ApplicationResponse<String>> forgotPassword(@RequestParam("username") String email,HttpSession session){
        MessageTemplateEnum enm = MessageTemplateEnum.FORGOT_PASSWORD;
        Map<String,String> hm = new HashMap<>();
        if(!this.userReadService.isUserAlreadyRegistered(email)){
            throw new ApplicationException("User not found.");
        }
        User user = this.userReadService.getUserByUsername(email);
        SmsSendingPojo smsPojo = this.mobileSmsService.sendMessage(user,enm,hm);
        if(smsPojo.getMessage().equalsIgnoreCase(ApplicationConstants.SUCCESS)==false || Objects.isNull(smsPojo.getRequestId()))
            return new ResponseEntity<>(ApplicationResponse.getFailureResponse("",400,"Issue in sending sms."), HttpStatus.BAD_REQUEST);
        session.setAttribute("username",user.getUsername());
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(smsPojo.getRequestId(),200,"Otp Sent to " + "******" + user.getMobileNumber().substring(6)),HttpStatus.OK);
    }

    @PostMapping(value = "/validate-otp")
    public ResponseEntity<ApplicationResponse<String>> validateOtp(@RequestBody OtpValidationRequestDto dto){
        ResponseEntity<ApplicationResponse<String>> response= null;
        String otpValidationMsg = this.mobileSmsService.validateOtpToken(dto.getReqId(), dto.getEnteredOtp());
        if(otpValidationMsg.equalsIgnoreCase(ApplicationConstants.SUCCESS)){
            response = new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null,HttpStatus.OK.value(),"Otp verification success."),HttpStatus.OK);
        }else {
            response = new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,HttpStatus.BAD_REQUEST.value(),otpValidationMsg),HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApplicationResponse<String>> changePassword(@RequestBody ChangePasswordRequestDto dto, HttpSession session){
        String username = (String)session.getAttribute("username");
        if(Objects.isNull(username)){
            throw new ApplicationException("Something went wrong.. please try again.");
        } else if(!dto.getPassword().equals(dto.getNewPassword())){
            throw new ApplicationException("password and confirm password should match.");
        } else if (dto.getPassword().length()<8) {
            throw new ApplicationException("password should be at least 8 character long.");
        }
        User user = this.userReadService.getUserByUsername(username);
        user.changePassword(dto.getPassword());
        this.userWriteService.saveUser(user);
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null,HttpStatus.OK.value(),"Password changes succesfully."),HttpStatus.OK);
    }

}
