package com.exceptions;

public class IncorrectAuthenticationException extends UnauthorizedUserException {

	private static final long serialVersionUID = 1L;

	public IncorrectAuthenticationException(String message) {
		super(message);
	}

}
