package com.repository;

import java.util.HashMap;

import com.exceptions.CustomerNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

/**
 * This repository manage the customer data(Create, Retrieve, Update and Delete).
 * @author idtaleb
 */
public interface CustomerRepository extends Repository {
	/**
	 * Find all customers in the data base.
	 * @return All the customers as a HashMap. 
	 *         Each key represents the user name of a customer
	 * 		   and its value is the corresponding customer instance.
	 */
	public HashMap<String, CustomerEntity> findAll();
	
	/**
	 * Get a customer specified by its ID
	 * @param username The user name of the customer
	 * @return Returns the corresponding customer
	 * @throws CustomerNotFoundException when the user name is not 
	 *         present in the list of registered customers.
	 */
	CustomerEntity getCustomerByName(String username) throws CustomerNotFoundException;
	
	/**
	 * Check if a customer is already signed in.
	 * @param username the user name of the customer to find.
	 * @return Returns true if the customer is already signed in, 
	 *         otherwise returns false. 
	 */
	public boolean isRegistered(String username);

	/**
	 * Save a customer into the data base.
	 * @param customer The customer to save.
	 * @return Returns true if the customer is well saved in the database, 
	 *         returns false otherwise.
	 * @throws InternalServerErrorException When an internal error occurs during the saving 
	 * 			the customer data.
	 */
	void save(CustomerEntity customer) throws InternalServerErrorException;
	
	/**
	 * Delete a customer from the database
	 * @param customer The customer to delete
	 * @throws InternalServerErrorException When an internal error occurs during deleting 
	 * 			the customer data.
	 */
	void delete(CustomerEntity customer) throws InternalServerErrorException;
}
