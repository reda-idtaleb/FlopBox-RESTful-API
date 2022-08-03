package com.exceptions;

public class NotFoundResourceException extends Exception{
	private static final String DEFAULT_MSG = "Resource not found!";
	private static final long serialVersionUID = 1L;

	public NotFoundResourceException() {
		this(DEFAULT_MSG);
	}

	public NotFoundResourceException(String message) {
		super(message);
	}

}
