package com.exceptions;

public class CannotDiconnectFromFTPServerException extends BadGatewayException {

	private static final long serialVersionUID = 1L;

	public CannotDiconnectFromFTPServerException() {
	}

	public CannotDiconnectFromFTPServerException(String message) {
		super(message);
	}


}
