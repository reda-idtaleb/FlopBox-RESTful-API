package com.utils;

import java.util.HashMap;
import java.util.Map;

public class AllowedDataFields {
	
	/** Allowed fields when creating or updating a FTP server */
	public static final String ALIAS_FIELD = "alias";
	
	/** Allowed fields when updating a FTP server */
	public static final String ADDRESS_FIELD = "address";
	public static final String PORT_FIELD = "port";
	
	/** Allowed fields when connecting to a the FlopBox server */
	public static final String APP_USERNAME_FIELD = "username";
	
	/** Allowed fields when connecting to a FTP server */;
	public static final String FTP_USERNAME_FIELD = "user";
	
	/** Field to determine a password **/
	public static final String PASSWORD_FIELD = "pwd";
	
	/** Allowed json data field when renaming a file */
	public static final String RENAME_FILE_FIELD = "renameTo";
	
	/** Allowed json data field when creating a directory in the ftp server */
	public static final String CREATE_DIR_FIELD = "dirName";
	
	/**
	 * Check if the fields of the JSON data are accepted.
	 * The allowed fields are "url" and/or "alias".
	 * @param data The received data from a customer
	 * @return returns true if all the fields of the data are accepted, false otherwise.
	 */
	public static boolean allFieldsAreAllowedWhenUpdateServer(HashMap<String, String> data) {
		for (Map.Entry<String, String> mapentry : data.entrySet()) {
			String field = mapentry.getKey();
			String regex = AllowedDataFields.ALIAS_FIELD+"|"+
						   AllowedDataFields.ADDRESS_FIELD+"|"+
						   AllowedDataFields.PORT_FIELD;
			if (!field.matches(regex))
				return false;
	    }
		return true;
	}
}
