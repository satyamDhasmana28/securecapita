package com.satyam.securecapita.user.RequestDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}
