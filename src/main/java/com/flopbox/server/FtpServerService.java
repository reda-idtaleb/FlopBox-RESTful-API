package com.flopbox.server;

import com.flopbox.common.ConflictException;
import com.flopbox.common.NotFoundException;
import com.flopbox.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FtpServerService {

    private final FtpServerRepository servers;

    public FtpServerService(FtpServerRepository servers) {
        this.servers = servers;
    }

    @Transactional(readOnly = true)
    public List<FtpServerResponse> list(AppUser user) {
        return servers.findAllByOwnerIdOrderByAliasAsc(user.getId()).stream()
                .map(FtpServerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FtpServerResponse get(AppUser user, String alias) {
        return FtpServerResponse.from(requiredServer(user, alias));
    }

    @Transactional
    public FtpServerResponse create(AppUser user, FtpServerRequest request) {
        if (servers.existsByOwnerIdAndAlias(user.getId(), request.alias())) {
            throw new ConflictException("A server with this alias already exists.");
        }
        FtpServer server = new FtpServer(
                user,
                request.alias(),
                request.host().trim(),
                request.portOrDefault()
        );
        return FtpServerResponse.from(servers.save(server));
    }

    @Transactional
    public FtpServerResponse update(AppUser user, String alias, FtpServerUpdateRequest request) {
        FtpServer server = requiredServer(user, alias);
        if (request.alias() != null
                && !request.alias().equals(alias)
                && servers.existsByOwnerIdAndAlias(user.getId(), request.alias())) {
            throw new ConflictException("A server with this alias already exists.");
        }
        server.update(request.alias(), request.host() == null ? null : request.host().trim(), request.port());
        return FtpServerResponse.from(server);
    }

    @Transactional
    public void delete(AppUser user, String alias) {
        servers.delete(requiredServer(user, alias));
    }

    @Transactional(readOnly = true)
    public FtpServer requiredServer(AppUser user, String alias) {
        return servers.findByOwnerIdAndAlias(user.getId(), alias)
                .orElseThrow(() -> new NotFoundException("No FTP server matches this alias."));
    }
}
