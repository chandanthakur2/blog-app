package com.example.blog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.blog.model.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${jwt-secret}")
    private String jwtSecret;

    @Autowired
    private UserService userService;

    public boolean validateJwtToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWT.require(algorithm)
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            // Token is invalid
            return false;
        }
    }

    public DecodedJWT decodeJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public User getUserFromToken(String token) {
        DecodedJWT decodedJWT = decodeJwtToken(token);
        String email = decodedJWT.getClaim("userEmail").asString();
        User user = userService.getUserByEmail(email);
        return user;
    }
}
