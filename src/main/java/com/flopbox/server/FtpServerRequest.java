package com.flopbox.server;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FtpServerRequest(
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "must contain only letters, numbers, dots, underscores, or hyphens")
        String alias,
        @NotBlank @Size(max = 255) String host,
        @Min(1) @Max(65535) Integer port
) {
    public int portOrDefault() {
        return port == null ? 21 : port;
    }
}
