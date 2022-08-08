package com.services.servers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.data.JsonDataBase;
import com.exceptions.AliasAlreadyExistException;
import com.exceptions.AliasNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.exceptions.JSONFieldValueException;
import com.exceptions.UnmatchedJSONKeysException;
import com.exceptions.UnmatchedJSONValuesException;
import com.repository.ServerRepository;
import com.services.Serviceable;
import com.services.customers.CustomerEntity;
import com.utils.AllowedDataFields;

public class FTPServerService implements Serviceable {
     
	protected ServerRepository serverRepository ;
	
	/**
	 * @param serverRepository
	 */
	public FTPServerService(ServerRepository serverRepository) {
		super();
		this.serverRepository = serverRepository;
	}

	/**
	 * Shows all the registered FTP servers of a customer
	 * @param customer The client who asks the request.
	 * @return a list of registered servers of the customer.
	 */
	public List<FTPServerEntity> showAllServers(CustomerEntity customer) {
		return serverRepository.findAll(customer);
	}

	/**
	 * Shows the informations of a server. 
	 * The informations contains the address, alias and the port of the FTP server
	 * @param alias The server alias for which the information will be displayed.
	 * @param customer The client that asks the request.
	 * @return returns the current information of the FTP server.
	 * @throws AliasNotFoundException When the alias of the fTP server is not found.
	 */
	public FTPServerEntity showServer(String alias, CustomerEntity customer) throws AliasNotFoundException {
		return serverRepository.getFTPServerByAlias(alias, customer);
	}
	
	/**
	 * Create a server and add it to the servers list of the customer
	 * @param newServer The new server to create
	 * @param customer The customer that asks the request.
	 * @throws UnmatchedJSONKeysException 
	 * @throws JSONFieldValueException 
	 * @throws UnmatchedJSONValuesException 
	 * @throws AliasAlreadyExistException 
	 */
	public FTPServerEntity createServer(FTPServerEntity newServer, CustomerEntity customer) 
			throws UnmatchedJSONKeysException, JSONFieldValueException, 
				   UnmatchedJSONValuesException, AliasAlreadyExistException {
		if (newServer.getAddress() == null)
			throw new UnmatchedJSONKeysException(UnmatchedJSONKeysException.errorMsg());
		else if (newServer.getAddress().isBlank())
			throw new JSONFieldValueException(JSONFieldValueException.errorMsg(AllowedDataFields.ADDRESS_FIELD));
		if (newServer.getPort().isBlank())
			throw new JSONFieldValueException(JSONFieldValueException.errorMsg(AllowedDataFields.PORT_FIELD));
		if (serverRepository.aliasExist(newServer.getAlias(), customer))
			throw new AliasAlreadyExistException(AliasAlreadyExistException.getDefaultMsg());
		if (!isValidPort(newServer.getPort()))
			throw new UnmatchedJSONValuesException(UnmatchedJSONValuesException.PORT_VALUE_ERROR);
		serverRepository.save(newServer, customer);
		return newServer;
	}
	
	/**
	 * Delete a FTP server from the servers list of a customer.
	 * @param alias The alias of the FTP server to delete
	 * @param customer The customer that asks the request
	 * @throws AliasNotFoundException when the given alias is not found among 
	 * 			the aliases created by the customer.
	 * @throws InternalServerErrorException When an internal error occurs during
	 * 			the deletion of the alias.
	 */
	public HashMap<String, FTPServerEntity> deleteServer(String alias, CustomerEntity customer) throws AliasNotFoundException, InternalServerErrorException {
		HashMap<String, FTPServerEntity> reply = new HashMap<>();
		FTPServerEntity removed = serverRepository.delete(alias, customer);
		reply.put("deletedServer", removed);
		return reply;
	}
	
	/**
	 * Update the informations of a FTP server. 
	 * The informations include the alias and the url of the FTP server.
	 * @param oldAlias The old alias of the server. 
	 * @param server The server to update.
	 * @param customer The customer that asks the request.
	 * @throws AliasNotFoundException when the given alias is not found among 
	 * 			the aliases created by the customer.
	 * @throws UnmatchedJSONKeysException when the fields(keys) of the data are not accepted.
	 * @throws JSONFieldValueException When the values of the keys are empty strings.
	 * @throws UnmatchedJSONValuesException When the value of the port is not an integer number.
	 * @throws InternalServerErrorException When an internal error occurs during the update operation.
	 */
	public HashMap<String, FTPServerEntity> updateServer(String oldAlias, 
														 HashMap<String, String> newData, 
														 CustomerEntity customer) 
											throws AliasNotFoundException, 
												   UnmatchedJSONKeysException, 
												   JSONFieldValueException, 
												   UnmatchedJSONValuesException, 
												   InternalServerErrorException {
		HashMap<String, FTPServerEntity> reply = new HashMap<>();
		
		String newAlias = newData.get(AllowedDataFields.ALIAS_FIELD);
		String newAddress = newData.get(AllowedDataFields.ADDRESS_FIELD);
		String newPort = newData.get(AllowedDataFields.PORT_FIELD);
		
		String oldInfoLabel = "oldFTPServerInfo"; 
		String newInfoLabel = "newFTPServerInfo";
		
		if (!serverRepository.aliasExist(oldAlias, customer))
			throw new AliasNotFoundException(oldAlias + " don't exist.");
		
		FTPServerEntity updatedServer = customer.getServers().remove(oldAlias);
		reply.put(oldInfoLabel, new FTPServerEntity(updatedServer));

		if ((newAlias == null && newAddress == null && newPort == null))
			throw new UnmatchedJSONKeysException(UnmatchedJSONKeysException.errorMsg());
		if (!AllowedDataFields.allFieldsAreAllowedWhenUpdateServer(newData))
			throw new UnmatchedJSONKeysException(UnmatchedJSONKeysException.errorMsg());
		if (newAlias!=null && newAlias.isBlank())
			throw new JSONFieldValueException(JSONFieldValueException.errorMsg(AllowedDataFields.ALIAS_FIELD));
		if (newAddress!=null && newAddress.isBlank())
			throw new JSONFieldValueException(JSONFieldValueException.errorMsg(AllowedDataFields.ADDRESS_FIELD));
		if (newPort!=null && newPort.isBlank())
			throw new JSONFieldValueException(JSONFieldValueException.errorMsg(AllowedDataFields.PORT_FIELD)); 
		if (newPort!=null && !isValidPort(newPort))
			throw new UnmatchedJSONValuesException(UnmatchedJSONValuesException.PORT_VALUE_ERROR);
		
		updatedServer = updateServerAssociation(newAlias, newAddress, newPort, updatedServer);
		reply.put(newInfoLabel, updatedServer);
		
		customer.addServer(updatedServer);
		serverRepository.update(customer);
		return reply;
	}
	
	/**
	 * Update the alias and/or the url of the server. 
	 * @param alias The alias of the FTP server to set. The alias mustn't be an empty string.
	 * @param address The address of the FTP server to set. The address mustn't be an empty string.Î
	 * @param server The FTP server instance to update. 
	 * @param port The new port of the FTP server.
	 * @return returns the updated FTP server instance.
	 */
	private FTPServerEntity updateServerAssociation(String alias, String address, String port, FTPServerEntity server) {
		if (address != null) 
			server.setAddress(address);
		if (alias != null) 
			server.setAlias(alias); 
		if (port != null) 
			server.setPort(port); 
		return server;
	}
	
	/**
	 * Check if the given port is an integer number.
	 * @param port the port to verify 
	 * @return return true if the given port is a valid number
	 */
	private boolean isValidPort(String port) {
		try {
			Integer.parseInt(port);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
}
