package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.user.Exception.PasswordViolationException;
import com.satyam.securecapita.user.Exception.RequestValidationException;
import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserValidator {
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final UserRepositoryWrapper userRepoWrapper;
    @Autowired
    public UserValidator(UserRepositoryWrapper userRepoWrapper) {
        this.userRepoWrapper = userRepoWrapper;
    }

    public String ValidateForUserRegistration(UserRegistrationRequestDto dto) {

        if (dto.getFirstName().isBlank() ) {
            throw new RequestValidationException("first name is mandatory.");
        } else if (dto.getLastName().isBlank()) {
            throw new RequestValidationException("last name is mandatory.");
        } else if (dto.getUsername().isBlank()) {
            throw new RequestValidationException("username is mandatory.");
        } else if (dto.getPassword().isBlank()) {
            throw new RequestValidationException("password is mandatory.");
        } else if(!isValidEmail(dto.getUsername())){
            throw new RequestValidationException("invalid email id.");
        } else if (this.userRepoWrapper.existsByEmailId(dto.getUsername())){
            throw new UserAlreadyRegisteredException("email id already registered.");
        }else if(!isValidatedPassword(dto.getPassword())){
            throw new PasswordViolationException("password must be Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character");
        }
        return ApplicationConstants.VALIDATED;
    }

    public boolean isValidatedPassword(String password) {
        // Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character
        Pattern pattern = Pattern.compile(UserValidator.PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(UserValidator.EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
