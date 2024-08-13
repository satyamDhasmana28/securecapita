package com.satyam.securecapita.user.responseDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticatedUserData {
    private Long userId; // primary key in table
    private String username; // email id
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime lastTimePasswordUpdated;
    private boolean isLogin;
    private boolean enabled; // email verification
    private LocalDateTime lastLoginFailed;
    private Set<String> roles;
    private String bearerToken; // jwt

}
