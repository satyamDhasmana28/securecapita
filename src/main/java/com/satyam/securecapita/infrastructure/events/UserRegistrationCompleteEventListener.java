package com.satyam.securecapita.infrastructure.events;

import com.satyam.securecapita.user.model.TokenVerification;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.TokenVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


@Component
@Slf4j
public class UserRegistrationCompleteEventListener implements ApplicationListener<UserRegistrationCompleteEvent> {

    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String supportGmail;
    private final TokenVerificationRepository tokenVerificationRepository;

    @Autowired
    public UserRegistrationCompleteEventListener(JavaMailSender mailSender, TokenVerificationRepository tokenVerificationRepository) {
        this.mailSender = mailSender;
        this.tokenVerificationRepository = tokenVerificationRepository;
    }

    @Override
    public void onApplicationEvent(UserRegistrationCompleteEvent event) {
        SecureRandom secureRandom = new SecureRandom();
        User user = event.getUser();
        // Generate random bytes
        byte[] randomBytes = new byte[64]; // Length can be adjusted based on requirements
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String verificationUrl = event.getVerificationUrl().concat("?token=" + token);
        TokenVerification tokenVerification = new TokenVerification(token,user);
//        save the token
        this.tokenVerificationRepository.save(tokenVerification);
        System.out.println(verificationUrl);
        //send it to email
        String subject = "Please Verify Your Email Address";
        // Email content (HTML)
        String message = "<html>" +
                "<body>" +
                "<h2>Welcome to Secure Capita, " + user.getFirstName() + "!</h2>" +
                "<p>Thank you for registering with us. To complete your registration, please verify your email address by clicking the link below:</p>" +
                "<a href=\"" + verificationUrl + "\">Verify Your Email</a>" +
                "<p>If you did not sign up for this account, please ignore this email.</p>" +
                "<br>" +
                "<p>Best regards,<br/>The Secure Capita Team</p>" +
                "</body>" +
                "</html>";

        // Create the email
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setFrom(supportGmail,"Secure Capital");
            helper.setTo(user.getEmailId()); // receiver emaid id
            helper.setSubject(subject);
            helper.setText(message, true); // The second argument indicates that the content is HTML

            // Send the email
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}