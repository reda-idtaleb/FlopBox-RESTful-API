package com.flopbox.ftp;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class FtpCredentialsResolver {

    private FtpCredentialsResolver() {
    }

    public static FtpCredentials fromHeaders(String username, String password) {
        if (username == null && password == null) {
            return FtpCredentials.anonymous();
        }
        if (username == null || password == null || username.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "X-Ftp-Username and X-Ftp-Password must be provided together."
            );
        }
        return new FtpCredentials(username, password);
    }
}
