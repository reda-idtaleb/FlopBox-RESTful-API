package com.flopbox.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FtpServerRepository extends JpaRepository<FtpServer, UUID> {

    List<FtpServer> findAllByOwnerIdOrderByAliasAsc(UUID ownerId);

    Optional<FtpServer> findByOwnerIdAndAlias(UUID ownerId, String alias);

    boolean existsByOwnerIdAndAlias(UUID ownerId, String alias);
}
