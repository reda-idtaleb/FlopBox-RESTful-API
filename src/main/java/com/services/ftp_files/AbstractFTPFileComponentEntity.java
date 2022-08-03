package com.services.ftp_files;

import java.util.Date;

import org.apache.commons.net.ftp.FTPFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class AbstractFTPFileComponentEntity {
	private static transient String uri; 
	
	private String name;
	protected transient int typeValue;
	private String type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String link;	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ssZZZ")
	private Date creationDate;
	private Long size;
	private String group;
	private String owner;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String hyperLink;
	private boolean userRead;
	private boolean groupRead;
	private boolean worldRead;
	private boolean userWrite;
	private boolean groupWrite;
	private boolean worldWrite;
	private boolean userExec;
	private boolean groupExec;
	private boolean worldExec;

	/**
	 * @param ftpFile initialize all the ftp file
	 */
	public void initializeAllfieldsWith(FTPFile ftpFile) {
		this.setName(ftpFile.getName());
		this.setOwner(ftpFile.getUser());
		this.setCreationDate(ftpFile.getTimestamp().getTime());
		this.setSize(ftpFile.getSize());
		this.setGroup(ftpFile.getGroup());
		this.typeValue = ftpFile.getType();
		this.setLink(ftpFile.getLink());
		this.setType(getFTPFileType());
		this.setUserRead(ftpFile.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION));
        this.setGroupRead(ftpFile.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION));
        this.setWorldRead(ftpFile.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.READ_PERMISSION));
        this.setUserWrite(ftpFile.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION));
        this.setGroupWrite(ftpFile.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION));
        this.setWorldWrite(ftpFile.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.WRITE_PERMISSION));
        this.setUserExec(ftpFile.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION));
        this.setGroupExec(ftpFile.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.EXECUTE_PERMISSION));
        this.setWorldExec(ftpFile.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.EXECUTE_PERMISSION));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the hyperLink
	 */
	 public String getHyperLink() {
		 return hyperLink;
	 }

	 /**
	  * @param hyperLink the hyperLink to set
	  */
	 public void setHyperLink(String hyperLink) {
		 this.hyperLink = uri+"/"+getUriFileResource()+"/"+hyperLink;
	 }
	 
	 /**
	  * @return returns the type of the ftp file.
	  */
	 protected abstract String getFTPFileType();
	 
	 /**
	  * @return returns the uri of the ftp file. 
	  */
	 protected abstract String getUriFileResource();
	 
	 /**
	  * Get the FTP file type
	  * @return returns the type of the ftp file
	  */
	 public String getType() {
		 return getFTPFileType();
	 }
	 
	 /**
	  * Set the type of the file
	  * @param type type to set
	  */
	 public void setType(String type) {
			this.type = type;
	 }
	 
	 /**
	 * @return the uri of the file resource
	 */
	 public static String getUri() {
		return uri;
	 }

	/**
	 * @param uri the uri to set
	 */
	 public static void setUri(String uri) {
		AbstractFTPFileComponentEntity.uri = uri;
	}
	 
	 @Override
	 public String toString() {
			 return "{type:" + type + ", name:" + name + ", creationDate:" + creationDate + ", size:"
					+ size + ", group:" + group + ", owner:" + owner + ", hyperLink:" + hyperLink + "}";
	 }

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	public boolean isUserRead() {
		return userRead;
	}

	public void setUserRead(boolean userRead) {
		this.userRead = userRead;
	}

	public boolean isGroupRead() {
		return groupRead;
	}

	public void setGroupRead(boolean groupRead) {
		this.groupRead = groupRead;
	}

	public boolean isWorldRead() {
		return worldRead;
	}

	public void setWorldRead(boolean worldRead) {
		this.worldRead = worldRead;
	}

	public boolean isUserWrite() {
		return userWrite;
	}

	public void setUserWrite(boolean userWrite) {
		this.userWrite = userWrite;
	}

	public boolean isGroupWrite() {
		return groupWrite;
	}

	public void setGroupWrite(boolean groupWrite) {
		this.groupWrite = groupWrite;
	}

	public boolean isWorldWrite() {
		return worldWrite;
	}

	public void setWorldWrite(boolean worldWrite) {
		this.worldWrite = worldWrite;
	}

	public boolean isUserExec() {
		return userExec;
	}

	public void setUserExec(boolean userExec) {
		this.userExec = userExec;
	}

	public boolean isGroupExec() {
		return groupExec;
	}

	public void setGroupExec(boolean groupExec) {
		this.groupExec = groupExec;
	}

	public boolean isWorldExec() {
		return worldExec;
	}

	public void setWorldExec(boolean worldExec) {
		this.worldExec = worldExec;
	}
}
