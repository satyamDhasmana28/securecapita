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
    private String errorMessage;
    private String username; //email id
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime lastTimePasswordUpdated;
    private boolean isLogin;
    private boolean enabled; //eamil verification
    private LocalDateTime lastLoginFailed;
    private Set<String> roles;

}
