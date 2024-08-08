package com.satyam.securecapita.user.serviceImpl;

import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public boolean existsByEmailId(String emailId){
        return this.repo.existsByEmailIdIgnoreCase(emailId);
    }

}
