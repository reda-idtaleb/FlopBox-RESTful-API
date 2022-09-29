package com.data;

public class DataBaseInfo {
	private final String LOCAL_ROOT = System.getProperty("user.dir");
	private final String SEPERATOR = System.getProperty("file.separator");
	private static final String DEFAULT_DB_FILENAME = "users_db.json";	
	
	private String filename;

	public DataBaseInfo() {
		this(DEFAULT_DB_FILENAME);
	}

	public DataBaseInfo(String filename) {
		this.filename = filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String getDatabasePath(){
		return LOCAL_ROOT + SEPERATOR + filename;
	}
}