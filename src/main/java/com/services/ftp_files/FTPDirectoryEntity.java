package com.services.ftp_files;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import com.fasterxml.jackson.annotation.JsonInclude;

public class FTPDirectoryEntity extends AbstractFTPFileComponentEntity {
	private static final String DIRECTORY_URI = "getDir";
	private static final String DIRECTORY_TYPE = "Directory";
	List<AbstractFTPFileComponentEntity> files;

	public FTPDirectoryEntity() {
		files = new ArrayList<AbstractFTPFileComponentEntity>();
	}
	
	/**
	 * @return get the ftp files of a ftp server
	 */
	public List<AbstractFTPFileComponentEntity> getFiles() {
		return files;
	}

	/**
	 * add a FTP file resource to the list of FTP files
	 * @param resource
	 */
	public void addResource(AbstractFTPFileComponentEntity resource) {
		files.add(resource);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	protected String getFTPFileType() {
		return DIRECTORY_TYPE;
	}

	@Override
	protected String getUriFileResource() {
		return DIRECTORY_URI;
	}


}
