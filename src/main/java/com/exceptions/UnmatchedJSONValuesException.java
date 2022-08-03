package com.exceptions;

import com.utils.AllowedDataFields;

public class UnmatchedJSONValuesException extends BadRequestException {
	public static final String PORT_VALUE_ERROR = "The value of the field " + AllowedDataFields.PORT_FIELD + " is not a number.";
	private static final long serialVersionUID = 1L;

	public UnmatchedJSONValuesException() {
	}
	
	public UnmatchedJSONValuesException(String string) {
		super(string);
	}

}
