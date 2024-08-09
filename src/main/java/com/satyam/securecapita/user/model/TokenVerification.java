package com.satyam.securecapita.user.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity(name = "m_token_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenVerification {
    private static final long TOKEN_EXPIRATION_TIME = 60 ; //  60 minute

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_token", nullable = false)
    private String verificationToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiration_time",nullable = false)
    private LocalDateTime expirationTime;

    public TokenVerification(String verificationToken, User user) {
        this.verificationToken = verificationToken;
        this.user = user;
        this.expirationTime = calcExpiration();
    }

    private LocalDateTime calcExpiration() {
        return LocalDateTime.now().plus(TokenVerification.TOKEN_EXPIRATION_TIME, ChronoUnit.MINUTES);
    }
}
