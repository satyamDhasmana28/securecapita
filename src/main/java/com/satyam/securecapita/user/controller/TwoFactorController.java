package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.security.CustomSecurityContext;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserRepository;
import com.satyam.securecapita.user.serviceImpl.UserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/two-factor")
public class TwoFactorController {

    private final CustomSecurityContext context;
    private final UserRepositoryWrapper userRepositoryWrapper;

    @Autowired
    public TwoFactorController(CustomSecurityContext context, UserRepositoryWrapper userRepositoryWrapper) {
        this.context = context;
        this.userRepositoryWrapper = userRepositoryWrapper;
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
}
