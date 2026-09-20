package com.flopbox.server;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FtpServerUpdateRequest(
        @Size(min = 1, max = 64)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "must contain only letters, numbers, dots, underscores, or hyphens")
        String alias,
        @Size(min = 1, max = 255) String host,
        @Min(1) @Max(65535) Integer port
) {
    @AssertTrue(message = "At least one of alias, host, or port must be provided.")
    public boolean hasChanges() {
        return alias != null || host != null || port != null;
    }
}
