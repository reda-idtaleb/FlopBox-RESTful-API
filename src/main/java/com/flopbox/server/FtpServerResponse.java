package com.flopbox.server;

import java.time.Instant;
import java.util.UUID;

public record FtpServerResponse(
        UUID id,
        String alias,
        String host,
        int port,
        Instant createdAt,
        Instant updatedAt
) {
    static FtpServerResponse from(FtpServer server) {
        return new FtpServerResponse(
                server.getId(),
                server.getAlias(),
                server.getHost(),
                server.getPort(),
                server.getCreatedAt(),
                server.getUpdatedAt()
        );
    }
}
