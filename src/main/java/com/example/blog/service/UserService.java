package com.example.blog.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.blog.dto.LoginDto;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt-secret}")
    private String jwtSecret;


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        return userRepository.save(existingUser);
    }

    private boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public String login(LoginDto loginDto) {
        try {
            User existingUser = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
            if (existingUser == null) {
                throw new RuntimeException("User not found");
            }

            if(!verifyPassword(existingUser, loginDto.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            // create a jwt token and return it
            return generateJwtToken(existingUser);
        }
        catch (Exception e) {
            throw new RuntimeException("Error logging in");
        }
    }

    private String generateJwtToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

        Instant now = Instant.now();
        Instant expiration = now.plus(24, ChronoUnit.HOURS);

        return JWT.create()
                .withSubject(user.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withClaim("userId", user.getId())
                .withClaim("userName", user.getName())
                .withClaim("userEmail", user.getEmail())
                .sign(algorithm);
    }
}
