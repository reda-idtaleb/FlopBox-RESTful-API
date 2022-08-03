package com.exceptions;

public class CannotListDirectoryException extends NotFoundResourceException {

	private static final long serialVersionUID = 1L;

	public CannotListDirectoryException() {
	}

	public CannotListDirectoryException(String message) {
		super(message);
	}

}
