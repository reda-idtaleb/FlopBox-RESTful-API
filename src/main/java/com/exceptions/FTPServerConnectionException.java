package com.exceptions;

public class FTPServerConnectionException extends BadGatewayException {

	private static final long serialVersionUID = 1L;


	public FTPServerConnectionException() {
		super();
	}

	public FTPServerConnectionException(String message) {
		super(message);
	}

}
