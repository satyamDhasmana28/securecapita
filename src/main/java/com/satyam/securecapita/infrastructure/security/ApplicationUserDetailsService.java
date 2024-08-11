package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Autowired
    public ApplicationUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = this.userRepo.findByEmailIdIgnoreCase(username);
        if(userOptional.isEmpty())
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("user name or email not found");
        return new org.springframework.security.core.userdetails.User(username,null,userOptional.get().getAuthorities());
    }
}
