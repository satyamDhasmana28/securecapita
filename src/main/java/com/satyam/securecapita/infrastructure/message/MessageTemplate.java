package com.satyam.securecapita.infrastructure.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "m_message_template")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name",nullable = false,unique = true)
    private String templateName;

    @Column(name = "msg_template",nullable = false)
    private String messageTemplate;

    @Column(name = "is_authorised")
    private boolean isAuthorised;

}
