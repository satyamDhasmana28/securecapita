package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryWrapper {
    private final UserRepository repo;

    @Autowired
    public UserRepositoryWrapper(UserRepository repo) {
        this.repo = repo;
    }

    public User save(User user){
        return this.repo.save(user);
    }

//    return true if user registered or email-verification not success
    public boolean existsByEmailId(String emailId){
        return this.repo.existsByEmailIdIgnoreCase(emailId);
    }

    public Optional<User> findByEmailId(String email){
        return this.repo.findByEmailIdIgnoreCase(email);
    }

}
