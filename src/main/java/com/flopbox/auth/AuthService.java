package com.flopbox.auth;

import com.flopbox.common.ConflictException;
import com.flopbox.security.JwtService;
import com.flopbox.user.AppUser;
import com.flopbox.user.AppUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("This username is already in use.");
        }
        AppUser user = users.save(new AppUser(request.username(), passwordEncoder.encode(request.password())));
        return tokenResponse(jwtService.issue(user));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        AppUser user = users.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password.");
        }
        return tokenResponse(jwtService.issue(user));
    }

    private TokenResponse tokenResponse(JwtService.AccessToken token) {
        return new TokenResponse(token.value(), "Bearer", token.expiresAt());
    }
}
