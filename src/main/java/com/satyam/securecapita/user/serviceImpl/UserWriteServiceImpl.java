package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.user.Exception.UserAlreadyRegisteredException;
import com.satyam.securecapita.user.RequestDto.UserRegistrationRequestDto;
import com.satyam.securecapita.user.model.TokenVerification;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.TokenVerificationRepository;
import com.satyam.securecapita.user.service.UserReadService;
import com.satyam.securecapita.user.service.UserWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserWriteServiceImpl implements UserWriteService {

    private final UserRepositoryWrapper userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenVerificationRepository tokenRepo;
    private final UserReadService userReadService;

    @Autowired
    public UserWriteServiceImpl(UserRepositoryWrapper userRepo, final PasswordEncoder passwordEncoder, TokenVerificationRepository tokenRepo, UserReadService userReadService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
        this.userReadService = userReadService;
    }

    @Transactional
    @Override
    public User doUserRegisteration(UserRegistrationRequestDto dto) throws UserAlreadyRegisteredException{
        User user = null;
        try {
            boolean isRegistered = this.userReadService.isUserAlreadyRegistered(dto.getUsername().trim());
            if(isRegistered){
                if(this.userReadService.isUserEmailVerified(dto.getUsername())){
                    throw new UserAlreadyRegisteredException("User Already registered. Please, login.");
                } else{
                    //delete previous token and return non-email-verified user
                    user = this.userRepo.findByEmailId(dto.getUsername()).get();
                    this.tokenRepo.deleteByUser(user);
                }
            }else{
                User newUser = User.builder().
                        createdAt(LocalDateTime.now()).
                        firstName(dto.getFirstName()).
                        lastName(dto.getLastName()).
                        emailId(dto.getUsername()).
                        password(this.passwordEncoder.encode(dto.getPassword())).build();
                user = this.userRepo.save(newUser);
            }

        } catch (Exception e) {
            if(e instanceof UserAlreadyRegisteredException){
                throw new UserAlreadyRegisteredException( ((UserAlreadyRegisteredException)e).getMessage() );
            }
            else{
                log.error("Exception in doUserRegisteration "+e.getMessage());
            }
        }
        return user;
    }

    @Override
    public String verifyEmailUsingToken(String token) {
        Optional<TokenVerification> optional = this.tokenRepo.findByVerificationToken(token);
        if(optional.isEmpty()){
            return "invalid token.";
        }
        TokenVerification tokenVerification = optional.get();
        // token expiration condition
        if(tokenVerification.getExpirationTime().isBefore(LocalDateTime.now())){
            return "Link has been expired.";
        } else if (tokenVerification.getUser().isEnabled()) {
            return "This account has been already verified. you can login with your credentials.";
        }else {
            User user = tokenVerification.getUser();
            user.setEnabled(true);
            this.userRepo.save(user);
        }
        return "email verification success.";
    }
}
