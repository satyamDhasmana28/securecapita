package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.events.UserRegistrationCompleteEvent;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.Exception.PasswordViolationException;
import com.satyam.securecapita.user.Exception.RequestValidationException;
import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;
import com.satyam.securecapita.user.RequestDto.LoginRequestData;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.responseDto.AuthenticatedUserData;
import com.satyam.securecapita.user.service.AuthenticationService;
import com.satyam.securecapita.user.service.UserRepository;
import com.satyam.securecapita.user.service.UserWriteService;
import com.satyam.securecapita.user.serviceImpl.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(ApplicationConstants.BASE_PATH)
public class UserController {

    private final UserValidator userValidator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserWriteService userWriteService;
    private final UserRepository userRepo;
    private final AuthenticationService authenticationService;
    @Autowired
    public UserController(UserValidator userValidator , final ApplicationEventPublisher applicationEventPublisher, final UserWriteService userWriteService, UserRepository userRepo, AuthenticationService authenticationService) {
        this.userValidator = userValidator;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userWriteService = userWriteService;
        this.userRepo = userRepo;
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody(required = false) UserRegistrationRequestDto dto, final HttpServletRequest httpServletRequest){
        String response=ApplicationConstants.SOMETHNG_WENT_WRONG;
        try{
            User savedUser =null;
//            validation the request json body
            this.userValidator.ValidateForUserRegistration(dto);
//            user creation login handles here
            savedUser = this.userWriteService.doUserRegisteration(dto);
//             publish a user registration complete event
            this.applicationEventPublisher.publishEvent(new UserRegistrationCompleteEvent(savedUser,this.createVerificationUrl(httpServletRequest)));
            response = "SUCCESS! Please, check your email to complete your registration.";
        } catch(RequestValidationException rve){
            response = rve.getMessage();
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        } catch (PasswordViolationException pve){
            response = pve.getMessage();
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        } catch (UserAlreadyRegisteredException e){
            return new ResponseEntity<>(e.getMessage() , HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            log.error("Exception in registerUser "+e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(400));
        }

        return new ResponseEntity<>(response,HttpStatus.valueOf(200));
    }

    @GetMapping("register/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        String response = ApplicationConstants.SOMETHNG_WENT_WRONG;
        try {
            if(token.isBlank()){
                return new ResponseEntity<>("blank token found.",HttpStatus.NOT_ACCEPTABLE);
            }
            response = this.userWriteService.verifyEmailUsingToken(token);
            if(response.equals("email verification success.")){
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e){
            log.error("Exception in /verifyEmail "+e.getMessage());
        }
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticatedUserData> authenticate(@RequestBody(required = false) LoginRequestData data){
        AuthenticatedUserData authenticatedUserData = null;
        try{
            authenticatedUserData = this.authenticationService.authenticate(data.getUsername(), data.getPassword());
        } catch (Exception e){
            if(e instanceof ApplicationException){
                return new ResponseEntity<>();
            }else if(e instanceof AuthenticationException){
                return new ResponseEntity<>(AuthenticatedUserData.builder().errorMessage(e.getMessage()).build() ,HttpStatus.UNAUTHORIZED);
            }

        }
        return new ResponseEntity<>(authenticatedUserData,HttpStatus.OK);
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
