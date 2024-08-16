package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.user.model.JwtSecretKey;
import com.satyam.securecapita.user.model.User;
import com.satyam.securecapita.user.service.JwtSecretKeyRepository;
import com.satyam.securecapita.user.serviceImpl.UserRepositoryWrapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwtParser;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

/*
*  this is jwt util used in token based authorisation.
* 1) it generates token and sign it with secret key
* 2) it verifies the token
*
* */
@Service
public class JwtUtil {

    private final UserDetailsService userDetailsService;
    private final JwtSecretKeyRepository jwtSecretKeyRepository;
    private final UserRepositoryWrapper userRepositoryWrapper;
    private static final long TOKEN_EXPIRATION_MILLIS = 1000*60*60*24*7; // 7 days

    @Autowired
    public JwtUtil(UserDetailsService userDetailsService, JwtSecretKeyRepository jwtSecretKeyRepository, UserRepositoryWrapper userRepositoryWrapper) {
        this.userDetailsService = userDetailsService;
        this.jwtSecretKeyRepository = jwtSecretKeyRepository;
        this.userRepositoryWrapper = userRepositoryWrapper;
    }


//    method will be called after authenticate method of ProviderManager
    public String generateToken(String username, List<String> roles){
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",roles);
        return createToken(username, claims);
    }

    private String createToken(String subject, Map<String, Object> claims) {
        String secretSigningKey = generateSecretKey(); //secret signing key
        User user = this.userRepositoryWrapper.findByEmailId(subject).get();
        if(Objects.isNull(user.getSecretKey())){ // if no corresponding JwtSecretKey(signing key) found
            JwtSecretKey jsk = JwtSecretKey.builder().user(user).secretKey(secretSigningKey).build();
            user.setSecretKey(jsk);
            this.userRepositoryWrapper.saveAndFlush(user);
        } else{ // update the secretSigningKey
            user.getSecretKey().setSecretKey(secretSigningKey);
            this.userRepositoryWrapper.saveAndFlush(user);
        }
        return Jwts.builder().
                setClaims(claims).
                setSubject(subject).
                setIssuedAt(new Date()).
                setExpiration(new Date(System.currentTimeMillis()+TOKEN_EXPIRATION_MILLIS)).
                signWith(SignatureAlgorithm.HS256,secretSigningKey).
                compact();
    }

    @Transactional
    public Claims extractAllClaims(String token){ //throws exception
        //extract sub(username or email) from the claims
        String username = extractUserEmailIdWithoutValidation(token);
        String secretKey = this.userRepositoryWrapper.findByEmailId(username).get().getSecretKey().getSecretKey();
        JwtParser parser = Jwts.parser();  // Get the JwtParser
        return parser
                .setSigningKey(secretKey)  // Set the secret key used to sign the token
                .parseClaimsJws(token)     // Parse the token and get the claims
                .getBody();
    }
    public <R> R extractClaims(String token, Function<Claims,R> claimsResolver){
        final Claims claims = extractAllClaims(token);
        R data = claimsResolver.apply(claims);
        System.out.println(data);
        return data;
    }

    public String extractUserEmailId(String token){
        return extractClaims(token, Claims::getSubject);
    }
    private String extractUserEmailIdWithoutValidation(String token) {
        String[] tokenParts = token.split("\\.");
//        String base64EncodedPayload = tokenParts[1]; // The payload part of the token
//        String payload = TextCodec.BASE64URL.decodeToString(base64EncodedPayload);

//        JwtParser parser = new DefaultJwtParser();
        // Parse the JSON payload into Claims
        Claims claims = Jwts.parser()
                .parseClaimsJwt(tokenParts[0] + "." + tokenParts[1] + ".") // Parsing only header and payload
                .getBody();
        return claims.getSubject(); // getting username or email
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return this.extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token , UserDetails userDetails){
        final String username = extractUserEmailId(token); // email
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private String generateSecretKey()  {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        keyGen.init(256); // for HMAC-SHA256
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
