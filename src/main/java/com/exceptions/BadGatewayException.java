package com.exceptions;

import java.io.IOException;

public class BadGatewayException extends IOException {

	private static final long serialVersionUID = 1L;

	public BadGatewayException() {
	}

	public BadGatewayException(String message) {
		super(message);
	}


}
