package com.satyam.securecapita.user.model;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity(name = "m_otp_token")
@Getter
@NoArgsConstructor
public class OtpToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_number",nullable = false)
    private String otpNumber;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(name = "otp_generation_dt_tm",nullable = false)
    private LocalDateTime otpGenerationDateTime;

    @Column(name = "otp_expiration_dt_tm",nullable = false)
    private LocalDateTime OtpExpirationDateTime;

    @Column(name = "failed_count",nullable = false, columnDefinition = "int default 0")
    private int failedAttemptCount;

    @Column(name = "purpose",nullable = false)
    private String purpose;

    public OtpToken(String otpNumber, long userId, String purpose){
        LocalDateTime localDateTime =  LocalDateTime.now();
        this.otpNumber =otpNumber;
        this.userId =userId;
        this.purpose =purpose;
        this.otpGenerationDateTime =localDateTime;
        this.OtpExpirationDateTime =localDateTime.plus(ApplicationConstants.OTP_VALIDITY, ChronoUnit.MINUTES);
    }

    public void wrongOtpAttempt(){
        this.failedAttemptCount++;
    }

    public boolean isOtpExpired(){
        return this.OtpExpirationDateTime.isBefore(LocalDateTime.now());
    }

}
