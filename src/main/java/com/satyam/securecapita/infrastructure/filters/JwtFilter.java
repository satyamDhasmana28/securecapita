package com.satyam.securecapita.infrastructure.filters;

import com.google.gson.Gson;
import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import com.satyam.securecapita.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    //cant be used here
    private final UserDetailsService userDetailsService;
    private String username;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String uriWithoutBasePath = requestUri.substring(ApplicationConstants.BASE_PATH.length());
        if (!nonTokenService().contains(uriWithoutBasePath)) {
            String authorizationHeader = request.getHeader("Authorization");
            if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.split(" ")[1];
                this.username = this.jwtUtil.extractUserEmailId(token);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                try {
                    this.jwtUtil.extractAllClaims(token); //throw exception in case of verifying token
                    boolean isTokenValid = this.jwtUtil.validateToken(token, userDetails);
                    if (isTokenValid) { // token validated
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.out.println("Jwt token invalid or expired");
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token invalid or expired.");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Exception in jwtFilter : " + e.getMessage());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: token tampered, signature not verify ");
                    return;
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Bearer token in missing.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        String message1 = new Gson().toJson(ApplicationResponse.getFailureResponse("UNAUTHORIZED",statusCode,message));
        response.getWriter().write(message1);
    }

    private static List<String> nonTokenService() {
        List<String> nonTokenBasedService = new ArrayList<>();
        nonTokenBasedService.add("/register");
        nonTokenBasedService.add("/register/verifyEmail");// token is generated in the login process
        nonTokenBasedService.add("/authenticate");
        nonTokenBasedService.add("/forgot/password");
        return nonTokenBasedService;
    }
}
