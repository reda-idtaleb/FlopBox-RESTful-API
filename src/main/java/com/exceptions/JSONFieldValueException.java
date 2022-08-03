package com.exceptions;

import com.utils.AllowedDataFields;

public class JSONFieldValueException extends BadRequestException {

	private static final long serialVersionUID = 1L;
	
	public JSONFieldValueException() {
	}

	
	public JSONFieldValueException(String string) {
		super(string);
	}

	public static String errorMsg(String addressField) {
		return "The value of the "+ addressField + " field cannot be an empty string.";
	}

}
