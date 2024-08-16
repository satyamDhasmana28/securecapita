package com.satyam.securecapita.user.service;

import com.satyam.securecapita.user.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken,Long> {
}
