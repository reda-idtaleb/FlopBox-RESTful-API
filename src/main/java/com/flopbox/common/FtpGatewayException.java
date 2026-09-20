package com.flopbox.common;

public class FtpGatewayException extends RuntimeException {

    public FtpGatewayException(String message) {
        super(message);
    }

    public FtpGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
