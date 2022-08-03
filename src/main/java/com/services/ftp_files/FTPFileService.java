package com.services.ftp_files;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.exceptions.AliasNotFoundException;
import com.exceptions.BadGatewayException;
import com.exceptions.BadRequestException;
import com.exceptions.CannotDiconnectFromFTPServerException;
import com.exceptions.CannotListDirectoryException;
import com.exceptions.CannotLoginException;
import com.exceptions.ConflictResourceException;
import com.exceptions.FTPServerAlreadyConnectedException;
import com.exceptions.FTPServerConnectionException;
import com.exceptions.InternalServerErrorException;
import com.exceptions.NotFoundResourceException;
import com.exceptions.UnmatchedJSONKeysException;
import com.repository.ServerRepository;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;
import com.services.servers.FTPServerService;
import com.services.servers.FTPServerSession;
import com.utils.AllowedDataFields;

public class FTPFileService extends FTPServerService {
	/** Defines the current FTP session of a customer */
	private static FTPServerSession ftpSession;
	
	public FTPFileService(ServerRepository serverRepository) {
		super(serverRepository);
	}

	/**
	 * Connect to a FTP server then login.
	 * @param alias The alias of the server to connect from
	 * @param user The username of the FTP server account
	 * @param pwd The password ofthe FTP server account
	 * @param customer The client that asks the request.
	 * @throws AliasNotFoundException when the given alias is not found among
	 * @throws IOException When a I/O error occurs during the connection to the FTP server
	 * @throws SocketException When an error occurs during the connection to the FTP server
	 * @throws CannotLoginException When the user or password are incorrect.
	 * @throws FTPServerConnectionException When the server refuse the connection.
	 * @throws FTPServerAlreadyConnectedException When a FTP server is already connected.
	 */
	public void connectToServer(String alias, String user, String pwd, CustomerEntity customer) 
			throws NotFoundResourceException, IOException, ConflictResourceException{
		if (!serverRepository.aliasExist(alias, customer))
			throw new AliasNotFoundException(alias + " don't exist.");
		FTPServerEntity server = customer.getServers().get(alias);
		ftpSession = new FTPServerSession(server);
		ftpSession.openSession(user, pwd);
	}
	
	/**
	 * Disconnect from the FTP server.
	 * @param alias The alias of the FTP server.
	 * @param customer The customer that asks the request.
	 * @throws AliasNotFoundException When the given alias is not found among
	 * @throws CannotDiconnectFromFTPServerException When an error during 
	 * 			the disconnection from the FTPserver.
	 */
	public void disconnectFromServer(String alias, CustomerEntity customer) 
			throws AliasNotFoundException, CannotDiconnectFromFTPServerException {
		if (!serverRepository.aliasExist(alias, customer))
			throw new AliasNotFoundException(alias + " don't exist.");
		ftpSession.closeSession(); 
	}
	
	/**
	 * List all files of a directory.
	 * @param alias The alias of the FTP server.
	 * @param path The path of a directory
	 * @param usr The user name of the FTPServer
	 * @param pwd The password of the FTPserver.
	 * @param customer The client that asks the request.
	 * @return returns a string containing all the files of the specified path
	 * @throws AliasNotFoundException when the given alias is not found among
	 * @throws IOException When a I/O error occurs during the connection to the FTP server
	 * @throws SocketException When an error occurs during the connection to the FTP server
	 * @throws CannotLoginException When the user or password are incorrect.
	 * @throws FTPServerConnectionException When the server refuse the connection.
	 * @throws FTPServerAlreadyConnectedException When a FTP server is already connected.
	 * @throws CannotListDirectoryException  When a directory cannot be listed.
	 * @throws CannotDiconnectFromFTPServerException When an error occurred during 
	 * 			the disconnection from the FTP server
	 */
	public List<AbstractFTPFileComponentEntity> listAllFiles(String alias, String path, String user, String pwd, CustomerEntity customer) 
			throws NotFoundResourceException, ConflictResourceException, IOException {
		connectToServer(alias, user, pwd, customer);
		List<AbstractFTPFileComponentEntity> list = ftpSession.list(path);
		disconnectFromServer(alias, customer);
		return list;
	}
	
	/**
	 * Download a file specified by its path from the server(alias). 
	 * @param alias The alias of the server
	 * @param path The pathname of the file.
	 * @param destPath The destination path where the files will be stored.
	 * @param user The user name of the FTP server
	 * @param pwd The password of the FTP server
	 * @param customer The customer who asked the request.
	 * @return The restored FTP file.
	 * @throws AliasNotFoundException when the given alias is not found among
	 * @throws IOException When a I/O error occurs during the connection to the FTP server
	 * @throws SocketException When an error occurs during the connection to the FTP server
	 * @throws CannotLoginException When the user or password are incorrect.
	 * @throws FTPServerConnectionException When the server refuse the connection.
	 * @throws FTPServerAlreadyConnectedException When a FTP server is already connected.
	 * @throws CannotDiconnectFromFTPServerException 
	 * @throws InternalServerErrorException When the local directory wich file are stored cannot 
	 * 			be created while storing a file.
	 */
	public InputStream downloadFile(String alias, 
									String path,
									String user, String pwd, 
									CustomerEntity customer) 
			throws NotFoundResourceException, ConflictResourceException, IOException {
		connectToServer(alias, user, pwd, customer);
		InputStream stored = ftpSession.downloadFile(alias, path);
		disconnectFromServer(alias, customer);
		return stored;		
	}
	
	/**
	 * Download a directory specified by it's path from a FTP server. 
	 * @param alias The alias of a FTP server
	 * @param path The path of the directory to download
	 * @param destPath The destination path where the files will be stored.
	 * @param user The customer user name.
	 * @param pwd The customer password.
	 * @param customer The customer who asked the request.
	 * @throws NotFoundResourceException When the FTP server alias is not found, 
	 * 									or when the directory is not found in the FTP server
	 * @throws ConflictResourceException When a FTP server is already connected.
	 * @throws IOException When an I/O error occurred  during the connection to the FTP server 
	 * 					or when disconnecting from the FTP server after downloading.
	 */
	public List<AbstractFTPFileComponentEntity> downloadDirectory(String alias, String path, 
														    String user, String pwd, 
														    CustomerEntity customer) 
			throws NotFoundResourceException, ConflictResourceException, IOException {
        FTPDirectoryEntity dir = new FTPDirectoryEntity();
		connectToServer(alias, user, pwd, customer);
		ftpSession.downloadDirectory(alias, path, dir);
		disconnectFromServer(alias, customer);
		return dir.getFiles();
	}
	
	/**
	 * Rename a FTP file.
	 * @param alias The alias of the ftp server.
	 * @param path The path of the file to rename.
	 * @param renameData the data containing the new name of the file.
	 * @param user the user name Of the ftp server.
	 * @param pwd the password Of the ftp server.
	 * @param customer The client who asked the request.
	 * @throws BadRequestException When the sent data are not valid, 
	 * 			because the format of JSON data wasn't respected.
	 * @throws IOException When an I/O error occurred during the renaming of the FTP file.
	 * @throws ConflictResourceException When the connection to the ftp server is already done.
	 * @throws NotFoundResourceException When the server or the file resource are not found.
	 */
	public void renameFile(String alias, String path, HashMap<String, String> renameData, 
								  String user, String pwd, CustomerEntity customer) 
			throws  BadRequestException, IOException, ConflictResourceException, NotFoundResourceException{
		if (renameData == null)
			throw new UnmatchedJSONKeysException("JSON data not specified.");
		if (!renameData.containsKey(AllowedDataFields.RENAME_FILE_FIELD) || renameData.keySet().size()!=1)
			throw new UnmatchedJSONKeysException("Invalid JSON properties (only '" + 
												AllowedDataFields.RENAME_FILE_FIELD + "' field allowed).");
		connectToServer(alias, user, pwd, customer);
		ftpSession.renameFile(path, renameData.get(AllowedDataFields.RENAME_FILE_FIELD));
		disconnectFromServer(alias, customer);
	}

	/**  
	 * 
	 * Delete a FTP directory.
	 * @param alias The alias of the ftp server.
	 * @param path The path of the file to rename.
	 * @param user the user name Of the ftp server.
	 * @param pwd the password Of the ftp server.
	 * @param customer The client who asked the request.
	 * @throws BadRequestException When the sent data are not valid, 
	 * 			because the format of JSON data wasn't respected.
	 * @throws IOException When an I/O error occurred during the deleting of the FTP file.
	 * @throws ConflictResourceException When the connection to the ftp server is already done.
	 * @throws NotFoundResourceException When the server or the file resource are not found.
	 */
	public void deleteDirectory(String alias, String path, String user, String pwd, CustomerEntity customer) 
			throws BadRequestException, IOException, ConflictResourceException, NotFoundResourceException {
		connectToServer(alias, user, pwd, customer);
		ftpSession.deleteDirectory(path);
		disconnectFromServer(alias, customer);
	}
	
	/**
	 * Create A FTP diretcory 
	 * @param alias The alias of the ftp server.
	 * @param path The path of the file to rename.
	 * @param user the user name Of the ftp server.
	 * @param pwd the password Of the ftp server.
	 * @param customer The client who asked the request.
	 * @throws UnmatchedJSONKeysException When the sent data are not valid, 
	 * 			because the format of JSON data wasn't respected.
	 * @throws NotFoundResourceException When the server alias or the path file are not found. 
	 * @throws BadGatewayException When cannot creating a directory. The ftp server refuse the command.
	 * @throws CannotDiconnectFromFTPServerException When cannot disconnecting from a FTP server. 
	 * 				may be because the ftp is already disconnected.
	 */
	public void createDirectory(String alias, String path, HashMap<String, String> newDirData, 
									   String user, String pwd, CustomerEntity customer) 
									   throws BadRequestException, IOException, ConflictResourceException, 
									   NotFoundResourceException{
		if (newDirData == null)
			throw new UnmatchedJSONKeysException("JSON data not specified.");
		if (!newDirData.containsKey(AllowedDataFields.CREATE_DIR_FIELD) || newDirData.keySet().size()!=1)
			throw new UnmatchedJSONKeysException("Invalid JSON properties (only '"  +
												  AllowedDataFields.CREATE_DIR_FIELD + "' field allowed).");
		connectToServer(alias, user, pwd, customer);
		String dirName = newDirData.get(AllowedDataFields.CREATE_DIR_FIELD);
		String dirPath = path.charAt(path.length()-1) == '/' ? path+dirName : path+"/"+dirName ;
		if(!ftpSession.createDirectory(dirPath)) {
			disconnectFromServer(alias, customer);
			throw new BadGatewayException("Cannot create the directory "+dirPath);
		}
		disconnectFromServer(alias, customer);
	}
	
	/**
	 * Upload a FIle to the ftp server.
	 * @param alias the alias of the ftp server
	 * @param path the path where the file will be uploaded
	 * @param file the file as an input stream to upload
	 * @param user the user name ftp server login
	 * @param pwd the password ftp server login
	 * @param customer the customer who asked the request.
	 * @throws IOException When an I/O error occurs during the storing of the file on the FTP server
	 * @throws NotFoundResourceException When the server alias or the path file are not found. 
	 * @throws ConflictResourceException When the FTP server is already connected.
	 */
	public void storeFile(String alias, String path, InputStream file, String user, String pwd,
			CustomerEntity customer) throws NotFoundResourceException, IOException, ConflictResourceException {
		connectToServer(alias, user, pwd, customer);
		if(!ftpSession.storeFile(path, file)) {
			disconnectFromServer(alias, customer);
			throw new BadGatewayException("Cannot upload the file.");
		}
		disconnectFromServer(alias, customer);
	}
	
	/**
	 * Upload a directory on the FTP server using a remote path
	 * @param alias The alias of the ftp server
	 * @param path the remote path to which the directory will be uploaded.
	 * @param user the username login of the ftp server
	 * @param pwd the password login of the ftp server
	 * @param file the zipped directory to be uploaded.
	 * @param customer the customer who asked the request.
	 * @throws IOException When an I/O error occurs during the storing of the file on the FTP server
	 * @throws NotFoundResourceException When the server alias or the path file are not found. 
	 * @throws ConflictResourceException When the FTP server is already connected.
	 */
	public void uploadDir(String alias, String path, String user, String pwd, 
			InputStream file, CustomerEntity customer) throws NotFoundResourceException, IOException, ConflictResourceException {
		connectToServer(alias, user, pwd, customer);
		ftpSession.uploadDirectory(alias, path, file);
		disconnectFromServer(alias, customer);
	}
	
	public Date getModificationTime(CustomerEntity customer, String alias, String user, String pwd, String filePath) 
			throws IOException, NotFoundResourceException, ConflictResourceException, AliasNotFoundException {
		connectToServer(alias, user, pwd, customer);
		Date modifiedDate = ftpSession.getModificationTime(filePath);
		disconnectFromServer(alias, customer);
		return modifiedDate;
	}
	
	
}
