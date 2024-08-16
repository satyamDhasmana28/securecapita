package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Getter
    private User user;
    @Autowired
    public ApplicationUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = this.userRepo.findByEmailIdIgnoreCase(username);
        if(userOptional.isEmpty()){
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("user name or email not found");
        }
        this.user = userOptional.get();
        return user;
//        return new org.springframework.security.core.userdetails.User(username,user.getPassword(),userOptional.get().getAuthorities());
    }
}
