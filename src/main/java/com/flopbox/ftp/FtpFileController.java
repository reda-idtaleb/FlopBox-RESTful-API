package com.flopbox.ftp;

import com.flopbox.security.CurrentUserService;
import com.flopbox.server.FtpServer;
import com.flopbox.server.FtpServerService;
import com.flopbox.user.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/servers/{alias}")
@Tag(name = "FTP files", description = "Stateless FTP file operations. Remote credentials are supplied per request and are not stored.")
public class FtpFileController {

    private final FtpFileService fileService;
    private final FtpServerService serverService;
    private final CurrentUserService currentUserService;

    public FtpFileController(
            FtpFileService fileService,
            FtpServerService serverService,
            CurrentUserService currentUserService
    ) {
        this.fileService = fileService;
        this.serverService = serverService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/files")
    @Operation(summary = "List one remote directory")
    public List<RemoteFileResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam(defaultValue = "/") String path,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        return fileService.list(server(jwt, alias), credentials(ftpUsername, ftpPassword), path);
    }

    @GetMapping("/files/tree")
    @Operation(summary = "Recursively list a remote directory up to the configured depth")
    public List<RemoteFileResponse> tree(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam(defaultValue = "/") String path,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        return fileService.tree(server(jwt, alias), credentials(ftpUsername, ftpPassword), path);
    }

    @GetMapping(value = "/files/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Stream a remote file to the caller")
    @ApiResponse(responseCode = "200", description = "Binary file stream")
    public ResponseEntity<StreamingResponseBody> download(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam String path,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        FtpServer server = server(jwt, alias);
        FtpCredentials credentials = credentials(ftpUsername, ftpPassword);
        StreamingResponseBody body = output -> fileService.download(server, credentials, path, output);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(safeFilename(path), StandardCharsets.UTF_8).build().toString()
                )
                .body(body);
    }

    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload one file to an explicit remote path")
    public ResponseEntity<Void> upload(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam String path,
            @RequestParam MultipartFile file,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) throws java.io.IOException {
        fileService.upload(server(jwt, alias), credentials(ftpUsername, ftpPassword), path, file.getInputStream());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/directories/archive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Safely extract a ZIP archive into a remote directory")
    public ResponseEntity<Void> uploadArchive(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam String path,
            @RequestParam MultipartFile archive,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) throws java.io.IOException {
        fileService.uploadArchive(server(jwt, alias), credentials(ftpUsername, ftpPassword), path, archive.getInputStream());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/directories")
    @Operation(summary = "Create a remote directory")
    public ResponseEntity<Void> createDirectory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @Valid @RequestBody DirectoryCreateRequest request,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        fileService.createDirectory(
                server(jwt, alias),
                credentials(ftpUsername, ftpPassword),
                request.path(),
                request.name()
        );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/files/rename")
    @Operation(summary = "Rename a remote file or directory without changing its parent directory")
    public ResponseEntity<Void> rename(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @Valid @RequestBody RenameRequest request,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        fileService.rename(server(jwt, alias), credentials(ftpUsername, ftpPassword), request.path(), request.newName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/directories")
    @Operation(summary = "Recursively delete one remote directory; the FTP root cannot be deleted")
    public ResponseEntity<Void> deleteDirectory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam String path,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        fileService.deleteDirectory(server(jwt, alias), credentials(ftpUsername, ftpPassword), path);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/last-modified")
    @Operation(summary = "Read the UTC modification time reported by the FTP server")
    public LastModifiedResponse lastModified(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String alias,
            @RequestParam String path,
            @RequestHeader(value = "X-Ftp-Username", required = false) String ftpUsername,
            @RequestHeader(value = "X-Ftp-Password", required = false) String ftpPassword
    ) {
        return fileService.lastModified(server(jwt, alias), credentials(ftpUsername, ftpPassword), path);
    }

    private FtpServer server(Jwt jwt, String alias) {
        AppUser user = currentUserService.requiredUser(jwt);
        return serverService.requiredServer(user, alias);
    }

    private FtpCredentials credentials(String username, String password) {
        return FtpCredentialsResolver.fromHeaders(username, password);
    }

    private String safeFilename(String path) {
        String lastPathSegment = path == null ? "download" : path.substring(path.lastIndexOf('/') + 1);
        return lastPathSegment.isBlank() ? "download" : lastPathSegment.replaceAll("[\\r\\n]", "_");
    }
}
