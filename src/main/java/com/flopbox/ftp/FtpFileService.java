package com.flopbox.ftp;

import com.flopbox.common.FtpGatewayException;
import com.flopbox.config.FtpProperties;
import com.flopbox.server.FtpServer;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FtpFileService {

    private static final DateTimeFormatter MODIFICATION_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final FtpHostPolicy hostPolicy;
    private final FtpProperties properties;

    public FtpFileService(FtpHostPolicy hostPolicy, FtpProperties properties) {
        this.hostPolicy = hostPolicy;
        this.properties = properties;
    }

    public List<RemoteFileResponse> list(FtpServer server, FtpCredentials credentials, String path) {
        String remotePath = normalizePath(path);
        return withClient(server, credentials, client -> toResponses(client.listFiles(remotePath), remotePath));
    }

    public List<RemoteFileResponse> tree(FtpServer server, FtpCredentials credentials, String path) {
        String remotePath = normalizePath(path);
        return withClient(server, credentials, client -> tree(client, remotePath, 0));
    }

    public void download(FtpServer server, FtpCredentials credentials, String path, OutputStream outputStream) {
        String remotePath = normalizePath(path);
        withClient(server, credentials, client -> {
            if (!client.retrieveFile(remotePath, outputStream)) {
                throw new FtpGatewayException("The remote file could not be downloaded.");
            }
            return null;
        });
    }

    public void upload(FtpServer server, FtpCredentials credentials, String path, InputStream inputStream) {
        String remotePath = requiredNonRootPath(path);
        withClient(server, credentials, client -> {
            if (!client.storeFile(remotePath, inputStream)) {
                throw new FtpGatewayException("The file could not be uploaded to the FTP server.");
            }
            return null;
        });
    }

    public void uploadArchive(FtpServer server, FtpCredentials credentials, String path, InputStream inputStream) {
        String destination = normalizePath(path);
        withClient(server, credentials, client -> {
            extractArchive(client, destination, inputStream);
            return null;
        });
    }

    public void createDirectory(FtpServer server, FtpCredentials credentials, String parentPath, String name) {
        String directoryName = validateName(name);
        String target = join(normalizePath(parentPath), directoryName);
        withClient(server, credentials, client -> {
            if (!client.makeDirectory(target)) {
                throw new FtpGatewayException("The remote directory could not be created.");
            }
            return null;
        });
    }

    public void rename(FtpServer server, FtpCredentials credentials, String path, String newName) {
        String source = requiredNonRootPath(path);
        String target = join(parentPath(source), validateName(newName));
        withClient(server, credentials, client -> {
            if (!client.rename(source, target)) {
                throw new FtpGatewayException("The remote file or directory could not be renamed.");
            }
            return null;
        });
    }

    public void deleteDirectory(FtpServer server, FtpCredentials credentials, String path) {
        String remotePath = requiredNonRootPath(path);
        withClient(server, credentials, client -> {
            deleteDirectory(client, remotePath);
            return null;
        });
    }

    public LastModifiedResponse lastModified(FtpServer server, FtpCredentials credentials, String path) {
        String remotePath = requiredNonRootPath(path);
        return withClient(server, credentials, client -> new LastModifiedResponse(remotePath, modificationTime(client, remotePath)));
    }

    private <T> T withClient(FtpServer server, FtpCredentials credentials, FtpOperation<T> operation) {
        FTPClient client = new FTPClient();
        try {
            client.setConnectTimeout(Math.toIntExact(properties.connectTimeout().toMillis()));
            client.setDefaultTimeout(Math.toIntExact(properties.socketTimeout().toMillis()));
            client.setSoTimeout(Math.toIntExact(properties.socketTimeout().toMillis()));
            InetAddress address = hostPolicy.resolveAllowedHost(server.getHost());
            client.connect(address, server.getPort());
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                throw new FtpGatewayException("The FTP server rejected the connection.");
            }
            if (!client.login(credentials.username(), credentials.password())) {
                throw new FtpGatewayException("The FTP server rejected the supplied credentials.");
            }
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            return operation.apply(client);
        } catch (FtpGatewayException exception) {
            throw exception;
        } catch (IOException | ArithmeticException exception) {
            throw new FtpGatewayException("The FTP operation failed.", exception);
        } finally {
            close(client);
        }
    }

    private List<RemoteFileResponse> tree(FTPClient client, String path, int depth) throws IOException {
        List<RemoteFileResponse> response = new ArrayList<>();
        for (FTPFile file : client.listFiles(path)) {
            if (isDotDirectory(file)) {
                continue;
            }
            String filePath = join(path, file.getName());
            List<RemoteFileResponse> children = null;
            if (file.isDirectory() && depth < properties.maxTreeDepth()) {
                children = tree(client, filePath, depth + 1);
            }
            response.add(toResponse(file, filePath, children));
        }
        return response;
    }

    private List<RemoteFileResponse> toResponses(FTPFile[] files, String parent) {
        List<RemoteFileResponse> response = new ArrayList<>();
        for (FTPFile file : files) {
            if (!isDotDirectory(file)) {
                response.add(toResponse(file, join(parent, file.getName()), null));
            }
        }
        return response;
    }

    private RemoteFileResponse toResponse(FTPFile file, String path, List<RemoteFileResponse> children) {
        Instant modifiedAt = file.getTimestamp() == null ? null : file.getTimestamp().toInstant();
        String type = file.isDirectory() ? "DIRECTORY"
                : file.getType() == FTPFile.SYMBOLIC_LINK_TYPE ? "SYMLINK" : "FILE";
        return new RemoteFileResponse(
                file.getName(),
                path,
                type,
                file.getSize(),
                modifiedAt,
                file.getUser(),
                file.getGroup(),
                children
        );
    }

    private void deleteDirectory(FTPClient client, String path) throws IOException {
        for (FTPFile file : client.listFiles(path)) {
            if (isDotDirectory(file)) {
                continue;
            }
            String child = join(path, file.getName());
            if (file.isDirectory()) {
                deleteDirectory(client, child);
            } else if (!client.deleteFile(child)) {
                throw new FtpGatewayException("A file inside the remote directory could not be deleted.");
            }
        }
        if (!client.removeDirectory(path)) {
            throw new FtpGatewayException("The remote directory could not be deleted.");
        }
    }

    private void extractArchive(FTPClient client, String destination, InputStream inputStream) throws IOException {
        try (ZipInputStream archive = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = archive.getNextEntry()) != null) {
                String relativePath = normalizeArchivePath(entry.getName());
                String target = join(destination, relativePath);
                if (entry.isDirectory()) {
                    ensureDirectory(client, target);
                } else {
                    ensureDirectory(client, parentPath(target));
                    if (!client.storeFile(target, archive)) {
                        throw new FtpGatewayException("An archive entry could not be uploaded.");
                    }
                }
                archive.closeEntry();
            }
        }
    }

    private void ensureDirectory(FTPClient client, String directory) throws IOException {
        if ("/".equals(directory)) {
            return;
        }
        String current = "";
        for (String segment : directory.substring(1).split("/")) {
            current = current + "/" + segment;
            if (!client.changeWorkingDirectory(current) && !client.makeDirectory(current)) {
                throw new FtpGatewayException("A directory from the archive could not be created.");
            }
        }
    }

    private Instant modificationTime(FTPClient client, String path) throws IOException {
        String reply = client.getModificationTime(path);
        if (reply == null) {
            throw new FtpGatewayException("The modification time could not be obtained.");
        }
        String timestamp = reply.replaceFirst("^\\d{3}\\s+", "").trim();
        try {
            return LocalDateTime.parse(timestamp, MODIFICATION_TIME).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException exception) {
            throw new FtpGatewayException("The FTP server returned an invalid modification time.", exception);
        }
    }

    private void close(FTPClient client) {
        if (!client.isConnected()) {
            return;
        }
        try {
            client.logout();
        } catch (IOException ignored) {
            // The connection is closed below.
        }
        try {
            client.disconnect();
        } catch (IOException ignored) {
            // There is no recoverable action left for a closed request.
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank() || "\0".equals(path)) {
            return "/";
        }
        if (path.indexOf('\0') >= 0 || path.contains("\\")) {
            throw new FtpGatewayException("The remote path is invalid.");
        }
        String normalized = (path.startsWith("/") ? path : "/" + path).replaceAll("/{2,}", "/");
        for (String segment : normalized.split("/")) {
            if (".".equals(segment) || "..".equals(segment)) {
                throw new FtpGatewayException("The remote path cannot contain traversal segments.");
            }
        }
        return normalized.length() > 1 && normalized.endsWith("/")
                ? normalized.substring(0, normalized.length() - 1)
                : normalized;
    }

    private String requiredNonRootPath(String path) {
        String normalized = normalizePath(path);
        if ("/".equals(normalized)) {
            throw new FtpGatewayException("The root directory is not valid for this operation.");
        }
        return normalized;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.contains("\\")
                || ".".equals(name) || "..".equals(name)) {
            throw new FtpGatewayException("The remote file name is invalid.");
        }
        return name;
    }

    private String normalizeArchivePath(String entryName) {
        if (entryName == null || entryName.isBlank() || entryName.startsWith("/") || entryName.contains("\\")) {
            throw new FtpGatewayException("The archive contains an invalid entry.");
        }
        String normalized = normalizePath(entryName);
        if ("/".equals(normalized)) {
            throw new FtpGatewayException("The archive contains an invalid entry.");
        }
        return normalized.substring(1);
    }

    private String join(String parent, String child) {
        String normalizedParent = normalizePath(parent);
        return "/".equals(normalizedParent) ? "/" + child : normalizedParent + "/" + child;
    }

    private String parentPath(String path) {
        int separator = path.lastIndexOf('/');
        return separator <= 0 ? "/" : path.substring(0, separator);
    }

    private boolean isDotDirectory(FTPFile file) {
        return ".".equals(file.getName()) || "..".equals(file.getName());
    }

    @FunctionalInterface
    private interface FtpOperation<T> {
        T apply(FTPClient client) throws IOException;
    }
}
