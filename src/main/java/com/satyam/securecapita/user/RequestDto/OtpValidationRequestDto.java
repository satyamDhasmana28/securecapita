package com.satyam.securecapita.user.RequestDto;

import lombok.Data;

@Data
public class OtpValidationRequestDto {
    private long reqId;
    private String enteredOtp;
}
