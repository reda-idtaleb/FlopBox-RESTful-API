package com.exceptions;

import com.utils.AllowedDataFields;

public class UnmatchedJSONKeysException extends BadRequestException {
	private static final String DEFAULT_MSG = "Some fields of JSON data are not recognized";
	private static final long serialVersionUID = 1L;
	
	public UnmatchedJSONKeysException() {
	}
	
	public UnmatchedJSONKeysException(String string) {
		super(string);
	}

	/**
	 * @return the defaultMsg
	 */
	public static String getDefaultMsg() {
		return DEFAULT_MSG;
	}

	public static String errorMsg() {
		// TODO Auto-generated method stub
		return getDefaultMsg() + "(only '" + AllowedDataFields.ALIAS_FIELD + "', '" 
		 		  + AllowedDataFields.ADDRESS_FIELD + "', '"
		 		  + AllowedDataFields.PORT_FIELD + "' are accepted)";
	}
	
	

}
