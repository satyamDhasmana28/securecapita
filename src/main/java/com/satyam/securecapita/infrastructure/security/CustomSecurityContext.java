package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.user.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CustomSecurityContext {
    public com.satyam.securecapita.user.model.User getAuthenticatedUser(){
        User user=null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!Objects.isNull(authentication) && authentication.isAuthenticated()){
            Object principal = authentication.getPrincipal();
            if(principal instanceof UserDetails){
                user = (User)principal;
            } else{
                throw new RuntimeException("principle not instance of UserDetails");
            }
        } else{
            throw new RuntimeException("null or unauthenticated user!!!!");
        }
        return Optional.ofNullable(user).orElseThrow(() -> new SecurityException("user is null`"));
    }
}
