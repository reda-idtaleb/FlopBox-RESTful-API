package com.exceptions;

public class AliasNotFoundException extends NotFoundResourceException {
	
	private static final long serialVersionUID = 1L;
	
	public AliasNotFoundException() {
	}
	
	public AliasNotFoundException(String string) {
		super(string);
	}

	public static String getDefaultMsg(String alias) {
		return alias + " don't exist.";
	}


}
