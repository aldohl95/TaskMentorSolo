package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.AuthResponse;
import com.taskmentor.taskmentor.dto.LoginRequest;
import com.taskmentor.taskmentor.dto.RegisterRequest;
import com.taskmentor.taskmentor.enums.AccountType;
import com.taskmentor.taskmentor.exception.ConflictException;
import com.taskmentor.taskmentor.model.User;
import com.taskmentor.taskmentor.repository.UserRepository;
import com.taskmentor.taskmentor.security.AuthUser;
import com.taskmentor.taskmentor.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // The role string is validated by RegisterRequest, so valueOf cannot fail here.
        user.setAccountType(AccountType.valueOf(request.getRole().toUpperCase()));
        user = userRepository.save(user);

        return toResponse(user.getEmail(), user.getAccountType(), user.getUserId());
    }

    /** Throws BadCredentialsException (mapped to 401) for an unknown email or a wrong password. */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        AuthUser user = (AuthUser) authentication.getPrincipal();

        return toResponse(user.email(), user.role(), user.userId());
    }

    private AuthResponse toResponse(String email, AccountType role, Long userId) {
        return new AuthResponse(jwtUtil.generateToken(email, role.name()), email, role.name(), userId);
    }
}
