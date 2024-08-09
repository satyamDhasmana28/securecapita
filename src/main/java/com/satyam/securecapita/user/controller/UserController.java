package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.events.UserRegistrationCompleteEvent;
import com.satyam.securecapita.user.Exception.PasswordViolationException;
import com.satyam.securecapita.user.Exception.RequestValidationException;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserWriteService;
import com.satyam.securecapita.user.serviceImpl.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/register")
public class UserController {

    private final UserValidator userValidator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserWriteService userWriteService;

    @Autowired
    public UserController(UserValidator userValidator ,final ApplicationEventPublisher applicationEventPublisher, final UserWriteService userWriteService) {
        this.userValidator = userValidator;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userWriteService = userWriteService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody(required = false) UserRegistrationRequestDto dto, final HttpServletRequest httpServletRequest){
        String response=ApplicationConstants.SOMETHNG_WENT_WRONG;
        try{
//            validation the request json body
            this.userValidator.ValidateForUserRegistration(dto);
//            saving the user in db
            User savedUser = this.userWriteService.doUserRegisteration(dto);
//             publish a user registration complete event
            this.applicationEventPublisher.publishEvent(new UserRegistrationCompleteEvent(savedUser,this.createVerificationUrl(httpServletRequest)));

        } catch(RequestValidationException rve){
            response = rve.getMessage();
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        } catch (PasswordViolationException pve){
            response = pve.getMessage();
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        } catch(Exception e){
            log.error("Exception in registerUser "+e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        }

        return new ResponseEntity<>(response,HttpStatus.valueOf(200));
    }

    @GetMapping("verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){

    }

    private String createVerificationUrl(HttpServletRequest httpServletRequest){
        String verificationUrl = new StringBuilder().append("http://").
                append(httpServletRequest.getServerName()).
                append(":").
                append(httpServletRequest.getServerPort()).
                append(httpServletRequest.getRequestURI()).append("/verifyEmail").toString();
        return verificationUrl;
    }

}
