package com.satyam.securecapita.infrastructure.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsSendingPojo {
    private String message; //either success, or some error message
    private String requestId; // for otp verification
}
