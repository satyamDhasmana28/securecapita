package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.user.Exception.RequestValidationException;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.serviceImpl.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/")
public class UserController {

    private final UserValidator userValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserController(UserValidator userValidator ,final ApplicationEventPublisher applicationEventPublisher) {
        this.userValidator = userValidator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    //have to complete this
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequestDto dto){
        String response=ApplicationConstants.SOMETHNG_WENT_WRONG;
        try{
//            validation the request json body
            this.userValidator.ValidateForUserRegistration(dto);
//             publish a user registeration event

        } catch(RequestValidationException rve){
            response = rve.getMessage();
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        } catch(Exception e){
            log.error("Exception in registerUser "+e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        }

        return new ResponseEntity<>(response,HttpStatus.valueOf(200));
    }
}
