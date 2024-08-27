package com.satyam.securecapita.user.model;

import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Setter
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Setter
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinTable(
            name = "m_user_roles",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns =
            @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Setter
    @Column(name = "last_time_password_updated")
    private LocalDateTime lastTimePasswordUpdated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "is_login")
    private boolean isLogin;

    @Setter
    @Column(name = "credentials_non_expired", columnDefinition = "boolean default true")
    private boolean credentialsNonExpired;

    @Setter
    @Column(name = "enabled",columnDefinition = "boolean default false")
    private boolean enabled; // for email-verification

    @Setter
    @Column(name = "last_login_failed")
    private LocalDateTime lastLoginFailed;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private JwtSecretKey secretKey;

    @Column(name = "is_two_factor_enabled",columnDefinition = "boolean default false")
    @Getter
    @Setter
    private boolean twoFactorEnabled;

    @Getter
    @Setter
    @Column(name = "mobile_no")
    private String mobileNumber;

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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean addRole(Role role){
        return this.roles.add(role);
    }

    public boolean removeRole(Role role){
        return this.roles.remove(role);
    }
}
