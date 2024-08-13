package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
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
import java.util.Objects;

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
    public ResponseEntity<ApplicationResponse<String>> registerUser(@RequestBody(required = false) UserRegistrationRequestDto dto, final HttpServletRequest httpServletRequest){
        String message=ApplicationConstants.SOMETHING_WENT_WRONG_MSG;
        ApplicationResponse applicationResponse =ApplicationResponse.getFailureResponse(null,400,message);
        try{
            User savedUser =null;
//            validation the request json body
            this.userValidator.ValidateForUserRegistration(dto);
//            user creation login handles here
            savedUser = this.userWriteService.doUserRegisteration(dto);
//             publish a user registration complete event
            this.applicationEventPublisher.publishEvent(new UserRegistrationCompleteEvent(savedUser,this.createVerificationUrl(httpServletRequest)));
            message = "SUCCESS! Please, check your email to complete your registration.";
            applicationResponse =ApplicationResponse.getSuccessResponse(null,200,message);
        } catch(RequestValidationException rve){
            message = rve.getMessage();
            applicationResponse = ApplicationResponse.getFailureResponse(null,400,message);
        } catch (PasswordViolationException pve){
            message = pve.getMessage();
            applicationResponse = ApplicationResponse.getFailureResponse(null,400,message);
        } catch (UserAlreadyRegisteredException e){
            message = e.getMessage();
            applicationResponse = ApplicationResponse.getFailureResponse(null,400,message);
        } catch(Exception e){
            log.error("Exception in registerUser "+e.getMessage());
        }

        return new ResponseEntity<>(applicationResponse,HttpStatus.valueOf(applicationResponse.getStatusCode()));
    }

    @GetMapping("register/verifyEmail")
    public ResponseEntity<ApplicationResponse<String>> verifyEmail(@RequestParam String token){
        String message = ApplicationConstants.SOMETHING_WENT_WRONG_MSG;
        ApplicationResponse applicationResponse =ApplicationResponse.getFailureResponse(null,400,message);
        try {
            if(token.isBlank()){
                return new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,400,"Invalid token. please try again."),HttpStatus.valueOf(400));
            }
            message = this.userWriteService.verifyEmailUsingToken(token);
            if(message.equals("email verification success.")){
                return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(null,200,message),HttpStatus.valueOf(200));
            }else{
                return new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,400,message),HttpStatus.valueOf(400));
            }

        } catch (Exception e){
            log.error("Exception in /verifyEmail "+e.getMessage());
        }
        return new ResponseEntity<>(ApplicationResponse.getFailureResponse(message),HttpStatus.valueOf(400));
    }

    @PostMapping("authenticate")
    public ResponseEntity<ApplicationResponse<AuthenticatedUserData>> authenticate(@RequestBody(required = false) LoginRequestData data){
        AuthenticatedUserData authenticatedUserData = null;
        try{
            if(Objects.isNull(data) || Objects.isNull(data.getUsername()) || Objects.isNull(data.getPassword())){
                throw new ApplicationException("Username and password is mandatory.");
            }
            authenticatedUserData = this.authenticationService.authenticate(data.getUsername(), data.getPassword());
            if(Objects.isNull(authenticatedUserData))
                throw new RuntimeException("authenticatedUserData is null");
        } catch (Exception e){
            if(e instanceof ApplicationException){
                return new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,400,e.getMessage()),HttpStatus.valueOf(400));
            }else if(e instanceof AuthenticationException){
                return new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,401,"Invalid credentials.") ,HttpStatus.UNAUTHORIZED);
            }else{
                log.error("Exception in /authenticate : {}", e.getMessage());
                return new ResponseEntity<>(ApplicationResponse.getFailureResponse(null,400,ApplicationConstants.SOMETHING_WENT_WRONG_MSG) ,HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ApplicationResponse.getSuccessResponse(authenticatedUserData,200,new StringBuilder().append("Welcome ").append(authenticatedUserData.getFirstName()).append("!").toString()),HttpStatus.OK);
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
