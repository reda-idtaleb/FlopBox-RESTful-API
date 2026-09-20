package com.flopbox.security;

import com.flopbox.common.NotFoundException;
import com.flopbox.user.AppUser;
import com.flopbox.user.AppUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    private final AppUserRepository users;

    public CurrentUserService(AppUserRepository users) {
        this.users = users;
    }

    public AppUser requiredUser(Jwt jwt) {
        UUID userId;
        try {
            userId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            throw new NotFoundException("The authenticated user no longer exists.");
        }
        return users.findById(userId)
                .orElseThrow(() -> new NotFoundException("The authenticated user no longer exists."));
    }
}
