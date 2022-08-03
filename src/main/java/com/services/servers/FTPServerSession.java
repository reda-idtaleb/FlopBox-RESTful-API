package com.services.servers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.exceptions.BadGatewayException;
import com.exceptions.CannotDiconnectFromFTPServerException;
import com.exceptions.CannotListDirectoryException;
import com.exceptions.CannotLoginException;
import com.exceptions.CannotLogoutFromFTPServerException;
import com.exceptions.FTPServerAlreadyConnectedException;
import com.exceptions.FTPServerConnectionException;
import com.exceptions.NotFoundResourceException;
import com.services.ftp_files.AbstractFTPFileComponentEntity;
import com.services.ftp_files.FTPDirectoryEntity;
import com.services.ftp_files.FTPFileEntity;

public class FTPServerSession {
	private static final String ANONYMOUS_SERVER = "anonymous";
	private FTPClient ftpClient;
	private FTPServerEntity ftpServer;
	private boolean isOpened;

	public FTPServerSession(FTPServerEntity ftpServer) {
		isOpened = false;
		this.ftpServer = ftpServer;
		this.ftpClient = new FTPClient();
	}
	
	/**
	 * Open an FTP server session.
	 * @param user The username to use to login.
	 * @param pwd The password to use to login.
	 * @throws FTPServerConnectionException When the server refuse the connection.
	 * @throws IOException When a I/O error occurs during the connection to the FTP server
	 * @throws SocketException When an error occurs during the connection to the FTP server
	 * @throws CannotLoginException When the user or password are incorrect.
	 * @throws FTPServerAlreadyConnectedException When a FTP server is already connected.
	 */
	public void openSession(String user, String pwd) throws FTPServerConnectionException, SocketException, 
	IOException, CannotLoginException, FTPServerAlreadyConnectedException {
		if (!isOpened) {
			if (user == null && pwd == null)
				user = pwd = ANONYMOUS_SERVER;
			int reply;
			ftpClient.connect(ftpServer.getAddress(), Integer.parseInt(ftpServer.getPort()));
			if(!ftpClient.login(user, pwd)) {
				ftpClient.disconnect();
				this.isOpened = false;
				throw new CannotLoginException("Cannot login to the FTP server. Incorrect user or password.");
			}
			System.out.println(">> Connected to " + ftpServer.getAddress() + ".");
			System.out.print(">> " + ftpClient.getReplyString());
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				this.isOpened = false;
				throw new FTPServerConnectionException(ftpClient + " server refused connection.");
			}
			this.isOpened = true;
		}
		else 
			throw new FTPServerAlreadyConnectedException("Cannot reconnect. The server is already connected.");
	}


	/**
	 * Close the FTP server session.
	 * @throws CannotDiconnectFromFTPServerException When an error 
	 * 			occurred during the disconnection from the FTP server
	 */
	public void closeSession() throws CannotDiconnectFromFTPServerException {
		String errorMsg = "Cannot disconnect from " + ftpServer.getAddress() + " server. Not connected yet.";
		if (!this.isOpened) 
			throw new CannotDiconnectFromFTPServerException(errorMsg);
		if(this.isOpened) {
			try {
				if (!ftpClient.logout()) {
					ftpClient.disconnect();
					isOpened = false;
					throw new CannotLogoutFromFTPServerException("Cannot logout from " + ftpServer.getAddress() + " server");
				}
				System.out.println("<< Logout from " + ftpServer.getAddress() + ".");
				ftpClient.disconnect();
				System.out.println("<< Disconnected from " + ftpServer.getAddress() + ".");
				isOpened = false;
			} catch(IOException ioe) {
				throw new CannotDiconnectFromFTPServerException(errorMsg);
			}
		}
	}

	/**
	 * Enter local passive mode
	 * @throws FTPServerConnectionException when no connection is established with the FTP server.
	 */
	public void enterPassiveMode() throws FTPServerConnectionException {
		if (isOpened) 
			ftpClient.enterLocalPassiveMode();
		else 
			throw new FTPServerConnectionException("FTP server not connected yet.") ;
	}

	/**
	 * List files of a directory.
	 * @param pathname The path name of the directory to list.
	 * @return return a hash map representing the listed files. 
	 * 		   The key will contains the name of the file and the value will contains
	 * 		   all the file informations.
	 * @throws FTPServerConnectionException when no connection is established with the FTP server.
	 * @throws CannotListDirectoryException When a directory cannot be listed.
	 * @throws CannotDiconnectFromFTPServerException When an I/O error occurs while 
	 * 		   disconnecting from the FTP server.
	 */
	public List<AbstractFTPFileComponentEntity> list(String pathname) throws FTPServerConnectionException, 
	CannotListDirectoryException, CannotDiconnectFromFTPServerException {
		FTPDirectoryEntity dir = new FTPDirectoryEntity();
		HashMap<String, List<AbstractFTPFileComponentEntity>> listing = new HashMap<String, List<AbstractFTPFileComponentEntity>>();
		this.enterPassiveMode();
		try {
			FTPFile[] files = ftpClient.listFiles(pathname);
			for (FTPFile file : files) {
				AbstractFTPFileComponentEntity resource;
				if (file.isDirectory()) {
					resource = new FTPDirectoryEntity();
					resource.initializeAllfieldsWith(file);
					resource.setName(pathname.charAt(pathname.length()-1)=='/' 
										? pathname + file.getName() 
										: pathname + "/" + file.getName() );
				}
				else {
					resource = new FTPFileEntity();
					resource.initializeAllfieldsWith(file);
				}
				dir.addResource(resource); 
			}
			return dir.getFiles();
		} catch (IOException e) { 
			closeSession();
			throw new CannotListDirectoryException("Unable to list files in the specified directory.");
		}	
	}

	/**
	 * Download a File from the FTP server
	 * @param alias The alias of the FTP server.
	 * @param path The pathname of the FTP file to download
	 * @return returns the input stream containing the content ftp file. 
	 * @throws NotFoundResourceException When a file is not found in the FTP server.
	 * @throws CannotDiconnectFromFTPServerException When an exception during 
	 * 							        the disconnection from the FTP server.
	 */
	public InputStream downloadFile(String alias, String path) 
			throws NotFoundResourceException, CannotDiconnectFromFTPServerException {
		try {
			ftpClient.enterLocalPassiveMode();
			InputStream inputStream = ftpClient.retrieveFileStream(File.separator+path);
			System.out.println(ftpClient.getReplyString());
			if (ftpClient.getReplyCode()/100 != 1) {
				ftpClient.getReplyCode();
				closeSession();
				throw new NotFoundResourceException("Unable to retreive file at " + path);
			}
			try {			
				System.out.println("pending " + ftpClient.getReplyString());
				if(!ftpClient.completePendingCommand()) {
					closeSession();
					throw new BadGatewayException("Could not complete transfer of " + path);
				}
			} catch (IOException ex) {
				closeSession();
				throw new NotFoundResourceException("Unable to retreive file "+ex.getMessage());
			}
			return inputStream;
		} catch (IOException e) {
			closeSession();
			throw new NotFoundResourceException("Unable to retreive file "+ e.getMessage());
		}
	}

	/**
	 * Download a directory from the FTP server.
	 * @param alias The alias of the FTP server.
	 * @param path The pathname of the FTP file to download
	 * @FTPDirectoryEntity directory The parent directory, 
	 * 						always initialized with the default constructor.
	 * @throws IOException When a I/O error occurs during the connection to the FTP server
	 * @throws NotFoundResourceException 
	 * @throws FTPServerAlreadyConnectedException 
	 */
	public void downloadDirectory(String alias, String path, FTPDirectoryEntity directory) 
			throws IOException, NotFoundResourceException, FTPServerAlreadyConnectedException{
		try {
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			int cwdValue = ftpClient.cwd(File.separator+path);
			if (cwdValue == 550) {
				closeSession();
				throw new NotFoundResourceException("The folder " + path + " cannot be found.");
			}
			FTPFile[] ftpFiles = ftpClient.listFiles();
			directory.setName(ftpClient.printWorkingDirectory());
			for (FTPFile ftpFile : ftpFiles) {
				AbstractFTPFileComponentEntity resource;
				if (ftpFile.isDirectory()) {
					resource = new FTPDirectoryEntity();
					resource.initializeAllfieldsWith(ftpFile);
					String parentFilePath = buildFilePath(path, resource);
					resource.setHyperLink(parentFilePath);
					downloadDirectory(alias, parentFilePath, (FTPDirectoryEntity)resource);
				} 
				else {
					resource = new FTPFileEntity();
					resource.initializeAllfieldsWith(ftpFile);
					String filePath = buildFilePath(path, resource);
					resource.setHyperLink(filePath);
				}
				directory.addResource(resource);
			}
		} catch (IOException e) {
			closeSession();
			throw new NotFoundResourceException("Cannot found the file " + e);
		}
	}
	
	public Date getModificationTime(String filePath) throws IOException {
		String time = ftpClient.getModificationTime(filePath);
		if (time == null)
			throw new IOException("Cannot get the modified time of the file : " + filePath);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date modificationTime = dateFormat.parse(time);         
            return modificationTime;
        } catch (ParseException ex) {
           return null;
        }
	}
	
	/**
	 * Rename a file specified by its path to a new name
	 * @param oldPath the old path of the ftp file
	 * @param newPath the new path of the ftp file 
	 * @throws IOException When the connection is closed during the renaming.
	 */
	public void renameFile(String oldPath, String newPath) throws IOException {
		if (!this.ftpClient.rename(oldPath, newPath)) {
			closeSession();
			throw new BadGatewayException("Cannot rename the file " + oldPath);
		}
	}
	
	/**
	 * Delete a file from the FTP server.
	 * @param path The path of the FTP file.
	 * @throws IOException When an I/O error occurred during the deleting.
	 * @throws BadGatewayException When a FTP file cannot be deleted(maybe the not enough privileges)
	 */
	public void deleteDirectory(String path) throws BadGatewayException, IOException {
		FTPFile[] files;
		ftpClient.enterLocalPassiveMode();
		files = ftpClient.listFiles(path);
		for (FTPFile file : files) {
			if (file.isDirectory()) 
				deleteDirectory(path + "/" + file.getName());
			else {
				String filePath = path + "/" + file.getName();
				ftpClient.deleteFile(filePath);
			}
		}
		if(!this.ftpClient.removeDirectory(path)) {
			closeSession();
			throw new BadGatewayException("Cannot delete " + path + " directory."); 
		} 
	}
	
	/**
	 * Create a ftp directory.
	 * @param dirName The name of the directory to be created
	 * @throws BadGatewayException When cannot create th ftp directory
	 */
	public boolean createDirectory(String dirName) throws BadGatewayException {
		try {
			return ftpClient.makeDirectory(dirName);
		} catch (IOException e) {
			closeSession();
			throw new BadGatewayException("Cannot create the directory");
		}	
	}

	/**
	 * Build the the path of the FTP file
	 * @param path The path of the parent file.
	 * @param resource The file resource we want to construct its path to.
	 * @return returns the path file of the ftp file resource
	 */
	private String buildFilePath(String path, AbstractFTPFileComponentEntity resource) {
		return path.isEmpty() ? resource.getName(): 
			( path.charAt(path.length()-1)=='/' ? path + resource.getName() : 
				path+File.separator+resource.getName());
	}
	
	
	/**
	 * Store a file on the server using the given path and taking input from the given InputStream.
	 * @param path the path which the file will be stored.
	 * @param file the input stream to store.
	 * @return return true if the store operation is successfully done.
	 * @throws BadGatewayException When an I/O error occurs during the storing file on the FTP server..
	 */
	public boolean storeFile(String path, InputStream file) throws BadGatewayException {
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return ftpClient.storeFile(path, file);
	    } catch (IOException e) {
	    	closeSession();
	    	throw new BadGatewayException("I/O error during uploading file.");
	    }
	}
	
	/**
	 * Upload a full directory
	 * @param alias The alias of the ftp server to which the directory will be uploaded
	 * @param path The FTP server path to which the directcory will be uploaded.
	 * @param file The zipped directory to upload
	 * @throws IOException When an I/O error occurs during the un-zipping of the file
	 */
	public void uploadDirectory(String alias, String ftpPath, InputStream file) 
			throws IOException {
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    	InputStream ftpIn = file;
        ZipInputStream zipIn = new ZipInputStream(ftpIn);
    	// complete and verify the retrieve

        // make the output un-zipped directory, should be unique sibling of the target zip
        String outDir = ftpPath + "_" + System.currentTimeMillis() + "/";
        ftpClient.makeDirectory(outDir);
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
        	closeSession();
            throw new BadGatewayException("Cannot create " + outDir);
        }

        // write the un-zipped entries
        ZipEntry zEntry;
        while ((zEntry = zipIn.getNextEntry()) != null) {
        	try {
        		if (zEntry.isDirectory()) {
            		ftpClient.makeDirectory(outDir + zEntry.getName());
            		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            			closeSession();
                        throw new BadGatewayException("Cannot unzip the " + zEntry.getName());
            		}
            		ftpClient.changeWorkingDirectory(outDir + zEntry.getName());
            		zEntry = zipIn.getNextEntry();
        		}
            	OutputStream out = ftpClient.storeFileStream(outDir + zEntry.getName());
                zipIn.transferTo(out);
                out.close();
                if (!ftpClient.completePendingCommand()) {
                	zipIn.closeEntry();
                	zipIn.close();
                	closeSession();
                    throw new BadGatewayException("Cannot unzip the " + zEntry.getName());
                }
        	} catch (IOException e) {
        		zipIn.closeEntry();
        		zipIn.close();
        		closeSession();
        		throw new BadGatewayException("Cannot unzip the " + zEntry.getName());
        	}
        }
        zipIn.closeEntry();
        zipIn.close();
	}

	@Override
	public String toString() {
		return "\"ftpSession\": { "
				+ "\"isOpened\":" + "\"" + isOpened + "\""  
				+ ", \"ftpURL\":" + "\"" + ftpServer + "\"" + "}";
	}

	/**
	 * @return returns the FTP client
	 */
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * @return returns true if the FTP server session is opened.
	 */
	public boolean isOpened() {
		return isOpened;
	}

	/**
	 * @param bool the boolean to set.
	 */
	public void setOpened(boolean bool) {
		this.isOpened = bool;
	}

	/**
	 * @return the ftpServer
	 */
	public FTPServerEntity getFtpServer() {
		return ftpServer;
	}

}
