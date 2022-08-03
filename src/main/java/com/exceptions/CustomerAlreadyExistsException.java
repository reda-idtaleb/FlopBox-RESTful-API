package com.exceptions;

public class CustomerAlreadyExistsException extends ConflictResourceException {

	private static final long serialVersionUID = 1L;

	public CustomerAlreadyExistsException(String message) {
		super(message);
	}

}
