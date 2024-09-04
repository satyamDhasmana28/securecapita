package com.satyam.securecapita.user.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "m_jwt_token_key")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtSecretKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false ,unique = true)
    private User user;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;
}
