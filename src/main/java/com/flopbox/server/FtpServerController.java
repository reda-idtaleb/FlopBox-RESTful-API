package com.flopbox.server;

import com.flopbox.security.CurrentUserService;
import com.flopbox.user.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/servers")
@Tag(name = "FTP servers", description = "FTP server configurations owned by the authenticated user.")
public class FtpServerController {

    private final FtpServerService serverService;
    private final CurrentUserService currentUserService;

    public FtpServerController(FtpServerService serverService, CurrentUserService currentUserService) {
        this.serverService = serverService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's FTP servers")
    public List<FtpServerResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return serverService.list(currentUser(jwt));
    }

    @GetMapping("/{alias}")
    @Operation(summary = "Get one FTP server configuration")
    @ApiResponse(responseCode = "404", description = "Alias not found")
    public FtpServerResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable String alias) {
        return serverService.get(currentUser(jwt), alias);
    }

    @PostMapping
    @Operation(summary = "Register an FTP server")
    @ApiResponse(responseCode = "201", description = "Server registered")
    public ResponseEntity<FtpServerResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody FtpServerRequest request
    ) {
        FtpServerResponse created = serverService.create(currentUser(jwt), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{alias}")
                .buildAndExpand(created.alias())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{alias}")
    @Operation(summary = "Update an FTP server configuration")
    public FtpServerResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @Valid @RequestBody FtpServerUpdateRequest request
    ) {
        return serverService.update(currentUser(jwt), alias, request);
    }

    @DeleteMapping("/{alias}")
    @Operation(summary = "Remove an FTP server configuration")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String alias) {
        serverService.delete(currentUser(jwt), alias);
        return ResponseEntity.noContent().build();
    }

    private AppUser currentUser(Jwt jwt) {
        return currentUserService.requiredUser(jwt);
    }
}
