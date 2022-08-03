package com.exceptions;

public class AliasAlreadyExistException extends ConflictResourceException {

	private static final long serialVersionUID = 1L;

	public AliasAlreadyExistException() {
	}

	public AliasAlreadyExistException(String message) {
		super(message);
	}

	public static String getDefaultMsg() {
		return "The given alias seems to be already attached to a server.";
	}


}
