package com.satyam.securecapita.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "m_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinTable(
            name = "m_user_roles",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns =
            @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "last_time_password_updated")
    private LocalDateTime lastTimePasswordUpdated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_login")
    private boolean isLogin;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "last_login_failed")
    private LocalDateTime lastLoginFailed;

    public void login(){
        this.setLogin(true);
    }

    public void logout(){
        this.setLogin(false);
    }

    public void changePassword(String password){
        this.setPassword(password);
        this.setLastTimePasswordUpdated(LocalDateTime.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.emailId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
}
