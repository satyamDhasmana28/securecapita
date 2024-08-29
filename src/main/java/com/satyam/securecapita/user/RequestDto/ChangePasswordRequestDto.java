package com.satyam.securecapita.user.RequestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto {
    @JsonProperty("newPassword")
    private String newPassword;
    @JsonProperty("password")
    private String password;
}
