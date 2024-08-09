package com.satyam.securecapita.infrastructure.events;

import com.satyam.securecapita.user.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class UserRegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private String verificationUrl;
    public UserRegistrationCompleteEvent(User user, String verificationUrl) {
        super(user);
        this.user = user;
        this.verificationUrl = verificationUrl;
    }
}
