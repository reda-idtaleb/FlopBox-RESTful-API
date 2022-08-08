package com;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;

public class DummyFTPServerFixture {

	private static final String USERNAME = "fake";
	private static final String PASSWORD = "fake";
	private static final String ROOT_FILE = "c:\\data";
     
	private FakeFtpServer fakeFtpServer;
	private FileSystem fileSystem;
	private UserAccount user;
	
	public DummyFTPServerFixture() {
		fakeFtpServer = new FakeFtpServer();
		fileSystem = new WindowsFakeFileSystem();
		user = new UserAccount(USERNAME, PASSWORD, ROOT_FILE);
		this.buildDummyFTPServer();
	}
	
	public void start() {
		fakeFtpServer.start();
	}
	
	private void buildDummyFTPServer() {
		fakeFtpServer.addUserAccount(this.user);
		
	    fileSystem.add(new DirectoryEntry(ROOT_FILE));
	    fileSystem.add(new FileEntry(ROOT_FILE + "\\file.txt", "abcdef 1234567890"));
	    fileSystem.add(new FileEntry(ROOT_FILE + "\\image.jpg"));
	    
	    fakeFtpServer.setFileSystem(fileSystem);	   
	}
	
}
