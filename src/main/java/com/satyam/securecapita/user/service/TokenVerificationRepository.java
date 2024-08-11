package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.model.TokenVerification;
import com.satyam.securecapita.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificationRepository extends JpaRepository<TokenVerification,Long> {

    Optional<TokenVerification> findByVerificationToken(@NonNull String verificationToken);

    void deleteByUser(User user);
}
