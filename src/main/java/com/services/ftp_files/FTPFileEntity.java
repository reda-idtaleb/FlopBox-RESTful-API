package com.services.ftp_files;

import org.apache.commons.net.ftp.FTPFile;

public class FTPFileEntity extends AbstractFTPFileComponentEntity {
	
	private static final String URI_FILE = "getFile";
	private static final String FILE_TYPE = "Regular file";
	private static final String SYMBOLIC_TYPE = "Symbolic link";
	
	public FTPFileEntity() {
		super();
	}

	@Override
	protected String getFTPFileType() {
		return this.typeValue == FTPFile.FILE_TYPE ? FILE_TYPE : SYMBOLIC_TYPE;
	}

	@Override
	protected String getUriFileResource() {
		return URI_FILE;
	}
}
