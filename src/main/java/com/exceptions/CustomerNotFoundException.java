package com.exceptions;

public class CustomerNotFoundException extends UnauthorizedUserException {

	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException(String message) {
		super(message);
	}

}
