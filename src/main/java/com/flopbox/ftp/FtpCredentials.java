package com.flopbox.ftp;

public record FtpCredentials(String username, String password) {

    public static FtpCredentials anonymous() {
        return new FtpCredentials("anonymous", "anonymous");
    }
}
