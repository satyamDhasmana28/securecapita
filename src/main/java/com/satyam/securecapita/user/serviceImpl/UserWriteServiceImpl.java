package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserWriteServiceImpl implements UserWriteService {

    private final UserRepositoryWrapper userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserWriteServiceImpl(UserRepositoryWrapper userRepo, final PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User doUserRegisteration(UserRegistrationRequestDto dto) {
        User savedUser = null;
        try {
            User newUser = User.builder().
                    createdAt(LocalDateTime.now()).
                    firstName(dto.getFirstName()).
                    lastName(dto.getLastName()).
                    emailId(dto.getUsername()).
                    password(this.passwordEncoder.encode(dto.getPassword())).build();
            savedUser = this.userRepo.save(newUser);
        } catch (Exception e) {
            log.error("Exception in doUserRegisteration "+e.getMessage());
        }
        return savedUser;
    }
}
