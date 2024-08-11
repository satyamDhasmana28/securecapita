package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.TokenVerificationRepository;
import com.satyam.securecapita.user.service.UserReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserReadServiceImpl implements UserReadService {
    private final UserRepositoryWrapper userRepositoryWrapper;
    private final TokenVerificationRepository tokenRepo;

    @Autowired
    public UserReadServiceImpl(UserRepositoryWrapper userRepositoryWrapper, TokenVerificationRepository tokenRepo) {
        this.userRepositoryWrapper = userRepositoryWrapper;
        this.tokenRepo = tokenRepo;
    }

    @Override
    public boolean isUserAlreadyRegistered(String username){
        Optional<User> userOptional = this.userRepositoryWrapper.findByEmailId(username);
        if(userOptional.isEmpty())
            return false;
        return true;
    }

    @Override
    public boolean isUserEmailVerified(String username) {
        return this.userRepositoryWrapper.findByEmailId(username).get().isEnabled();
    }
}
