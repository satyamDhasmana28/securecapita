package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.security.ApplicationUserDetailsService;
import com.satyam.securecapita.infrastructure.security.JwtUtil;
import com.satyam.securecapita.user.Exception.ApplicationException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.responseDto.AuthenticatedUserData;
import com.satyam.securecapita.user.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserRepositoryWrapper userRepositoryWrapper, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepositoryWrapper = userRepositoryWrapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthenticatedUserData authenticate(String username, String password) {
        AuthenticatedUserData authenticatedUserData = null;
        Optional<User> userOptional = this.userRepositoryWrapper.findByEmailId(username);
        if (userOptional.isEmpty()) {
            throw new ApplicationException("User not found with email: " + username);
        } else if (!userOptional.get().isEnabled()) {
            throw new ApplicationException("User not enabled via email-verification.");
        }
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            User user = ((ApplicationUserDetailsService) this.userDetailsService).getUser();
            if (user.isLogin()) {
                throw new ApplicationException("User is already logged in from another device.");
            }else if(Objects.isNull(user.getLastTimePasswordUpdated())){
                user.setLastTimePasswordUpdated(LocalDateTime.now());
                this.userRepositoryWrapper.saveAndFlush(user);
            }
            LocalDateTime pwdExpiration = user.getLastTimePasswordUpdated().plus(ApplicationConstants.PASSWORD_EXPIRED_DURATION_DAYS, ChronoUnit.DAYS);
            if (pwdExpiration.isBefore(LocalDateTime.now())) {
                throw new ApplicationException("Password is expired, Please reset your password.");
            }
//            fetching user roles
            List<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());
//            create jwt token
            String bearerToken = this.jwtUtil.generateToken(username,roles);
            authenticatedUserData = AuthenticatedUserData.builder().userId(user.getId()).username(user.getEmailId()).
                    firstName(user.getFirstName()).lastName(user.getLastName()).lastTimePasswordUpdated(user.getLastTimePasswordUpdated()).
                    password(null).roles(new HashSet<>(roles)).bearerToken(bearerToken).build();
        }
        return authenticatedUserData;
    }
}
