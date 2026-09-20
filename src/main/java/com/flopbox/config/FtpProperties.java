package com.flopbox.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.ftp")
public record FtpProperties(
        @NotNull Duration connectTimeout,
        @NotNull Duration socketTimeout,
        @Min(1) @Max(20) int maxTreeDepth,
        boolean allowPrivateAddresses
) {
}
