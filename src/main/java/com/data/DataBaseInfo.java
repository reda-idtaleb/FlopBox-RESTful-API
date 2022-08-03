package com.data;

public class DataBaseInfo {

	private static final String SEPERATOR = System.getProperty("file.separator");
	private static final String FILENAME = "users_db.json";
	
	private static final String LOCAL_ROOT = System.getProperty("user.dir");
	public static final String DATA_BASE_PATH = LOCAL_ROOT+SEPERATOR+FILENAME;
	
}
