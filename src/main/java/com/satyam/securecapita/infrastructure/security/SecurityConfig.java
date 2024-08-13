package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.infrastructure.filters.JwtFilter;
import com.satyam.securecapita.infrastructure.filters.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig  /*extends WebSecurityConfigurerAdapter*/ {

    private final ApplicationUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(ApplicationUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf().disable()  // Disabling CSRF (Enable it if needed)
                .cors().disable()  // Disabling CORS (Enable it if needed)
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/securecapita/api/v1/register/**").permitAll()  // Public APIs
                        .antMatchers("/securecapita/api/v1/authenticate/**").permitAll()
                        .antMatchers("/securecapita/api/v1/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  // Role-based APIs
                        .anyRequest().hasAuthority("ROLE_SUPER_USER")  // SuperUser access
                ).addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class). // request logging filter
                addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class) // jwt logging filter
                .formLogin(withDefaults())  // Default login form
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
