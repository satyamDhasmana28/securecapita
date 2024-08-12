package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.infrastructure.security.ApplicationUserDetailsService;
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
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthenticatedUserData authenticate(String username, String password) {
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        if(authentication.isAuthenticated()){
            User user = ((ApplicationUserDetailsService)this.userDetailsService).getUser();
            /*
               check all validation including email verification,
               password expired, already logged in
            */
            if(user.isLogin()){
                throw new ApplicationException("User is already logged in.");
            }

            AuthenticatedUserData authenticatedUserData = AuthenticatedUserData.builder().firstName(user.getFirstName()).

        }
        return null;
    }
}
