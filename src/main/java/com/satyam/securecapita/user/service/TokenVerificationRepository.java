package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.model.TokenVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenVerificationRepository extends JpaRepository<TokenVerification,Long> {
}
