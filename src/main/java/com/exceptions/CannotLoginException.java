package com.exceptions;

public class CannotLoginException extends NotFoundResourceException {

	private static final long serialVersionUID = 1L;

	public CannotLoginException() {
	}

	public CannotLoginException(String message) {
		super(message);
	}

}
