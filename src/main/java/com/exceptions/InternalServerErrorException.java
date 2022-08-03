package com.exceptions;

public class InternalServerErrorException extends NotFoundResourceException {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String message) {
		super(message);
	}

}
