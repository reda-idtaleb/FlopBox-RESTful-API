package com.flopbox.server;

import com.flopbox.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ftp_servers",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_ftp_servers_owner_alias",
                columnNames = {"owner_id", "alias"}
        )
)
public class FtpServer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @Column(nullable = false, length = 64)
    private String alias;

    @Column(nullable = false, length = 255)
    private String host;

    @Column(nullable = false)
    private int port;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FtpServer() {
        // JPA
    }

    public FtpServer(AppUser owner, String alias, String host, int port) {
        this.owner = owner;
        this.alias = alias;
        this.host = host;
        this.port = port;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String alias, String host, Integer port) {
        if (alias != null) {
            this.alias = alias;
        }
        if (host != null) {
            this.host = host;
        }
        if (port != null) {
            this.port = port;
        }
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public String getAlias() {
        return alias;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
