package com.expensetracker.service;

import com.expensetracker.dto.auth.AuthResponse;
import com.expensetracker.dto.auth.LoginRequest;
import com.expensetracker.dto.auth.SignupRequest;
import com.expensetracker.entity.AuthToken;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ConflictException;
import com.expensetracker.exception.UnauthorizedException;
import com.expensetracker.repository.AuthTokenRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private final Duration tokenTtl = Duration.ofDays(30);

    public AuthService(UserRepository userRepository, AuthTokenRepository authTokenRepository) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.getEmail().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already registered");
        }

        User u = new User();
        u.setName(request.getName().trim());
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(u);
        return issueToken(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim();
        User u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), u.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return issueToken(u);
    }

    private AuthResponse issueToken(User user) {
        AuthToken t = new AuthToken();
        t.setUser(user);
        t.setToken(UUID.randomUUID().toString());
        t.setExpiresAt(Instant.now().plus(tokenTtl));
        authTokenRepository.save(t);
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), t.getToken());
    }
}

