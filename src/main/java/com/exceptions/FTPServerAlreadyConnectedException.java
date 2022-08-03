package com.exceptions;

public class FTPServerAlreadyConnectedException extends ConflictResourceException {

	private static final long serialVersionUID = 1L;

	public FTPServerAlreadyConnectedException() {
	}

	public FTPServerAlreadyConnectedException(String message) {
		super(message);
	}

}
