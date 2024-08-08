package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmailIdIgnoreCase(@NonNull String emailId);
}
