package com.exceptions;

public class CannotLogoutFromFTPServerException extends BadGatewayException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotLogoutFromFTPServerException() {
	}

	public CannotLogoutFromFTPServerException(String message) {
		super(message);
	}

}
