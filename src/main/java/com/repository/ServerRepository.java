package com.repository;

import java.util.List;

import com.exceptions.AliasNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

/**
 * This repository manage the server data of a customer
 * (Create, Retrieve, Update and Delete).
 * @author idtaleb
 */
public interface ServerRepository extends Repository {
	/**
	 * Get all servers of a customer
	 * @param customer The customer 
	 * @return Returns a list of servers
	 */
	public List<FTPServerEntity> findAll(CustomerEntity customer);
	
	/**
	 * Get the FTP server object corresponding to the given alias.
	 * @param alias The alias of the FTP server.
	 * @return Returns an instance of {@link FTPServerEntity} that 
	 * 		   corresponds the alias.
	 * @throws AliasNotFoundException When the alias of the fTP server is not found.
	 */
	public FTPServerEntity getFTPServerByAlias(String alias, CustomerEntity customer) throws AliasNotFoundException; 

	
	/**
	 * Check if the given alias exists in the client's server list.
	 * @param alias The alias to check
	 * @param customer The customer that asks the request.
	 * @return returns true if the alias exist, false otherwise.
	 */
	public boolean aliasExist(String alias, CustomerEntity customer);
	
	/**
	 * Save a new server in the list of the FTP servers of a customer.
	 * @param newServer The new server to save in database
	 * @param customer The customer to which the FTP server is added.
	 * @return True if the FTP server was added successfully in the database.
	 */
	public boolean save(FTPServerEntity newServer, CustomerEntity customer);
	
	/**
	 * Delete a FTP server from the servers list of a customer.
	 * @param alias The alias of the FTP server to delete
	 * @param customer The customer that asks the request
	 * @throws AliasNotFoundException when the given alias is not found among 
	 * 		   the aliases created by the customer.
	 * @throws InternalServerErrorException When an internal error occurs during 
	 * 			the delete operation.
	 */
	public FTPServerEntity delete(String alias, CustomerEntity customer) throws AliasNotFoundException, 
																				InternalServerErrorException;
	
	/**
	 * Update the servers list of a customer
	 * @param customer The customer that we want to update
	 * @throws InternalServerErrorException When an internal error occurs during 
	 * 			the update operation.
	 */
	public void update(CustomerEntity customer) throws InternalServerErrorException;
}

