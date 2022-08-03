package com.services.customers;

import com.authentication.PasswordEncryption;
import com.exceptions.CustomerAlreadyExistsException;
import com.exceptions.CustomerNotFoundException;
import com.exceptions.IncorrectAuthenticationException;
import com.exceptions.InternalServerErrorException;
import com.exceptions.JSONFieldValueException;
import com.exceptions.NotFoundResourceException;
import com.exceptions.UnmatchedJSONKeysException;
import com.repository.CustomerRepository;
import com.services.Serviceable;
import com.utils.AllowedDataFields;

public class CustomerService implements Serviceable {
	private CustomerRepository customerRepository;
	
	/**
	 * @param customerRepository
	 */
	public CustomerService(CustomerRepository customerRepository) {
		super();
		this.customerRepository = customerRepository;
	}

	/**
	 * @see CustomerRepository
	 */
	public CustomerEntity getCustomerByName(String username) throws CustomerNotFoundException{
		return customerRepository.getCustomerByName(username);
	}
	
	/**
	 * Register a customer in the database of users, if the customer is already exists so 
	 * a CustomerAlreadyExists exception is raised. 
	 * @param customer The customer to register into the databse
	 * @throws CustomerAlreadyExists when the customer is already exists 
	 * 		in the registered customers list. This occurs when the provided username 
	 * 		is already taken.
	 * @throws JSONFieldValueCannotBeEmptyString When the value of JSON fields are empty or blank.
	 * @throws UnmatchedJSONKeysException When the fields 'username' and 'password' 
	 * 			are not both present in JSON data.
	 * @throws InternalServerErrorException When an internal error occurs during the saving 
	 * 			the customer data.
	 */
	public void registerCustomer(CustomerEntity customer) 
			throws CustomerAlreadyExistsException, JSONFieldValueException, UnmatchedJSONKeysException, InternalServerErrorException{
		boolean isEmptyPwd = PasswordEncryption.verifyUserPassword("", customer.getPwd(), customer.getSalt());
		if (isEmptyPwd || customer.getUsername() == null)
			throw new UnmatchedJSONKeysException("The fields '" 
												  + AllowedDataFields.APP_USERNAME_FIELD + "' and '" 
												  + AllowedDataFields.PASSWORD_FIELD + "' not found or are empty.");
		if (customer.getUsername().isBlank())
			throw new JSONFieldValueException("Some field values are blank or empty.");
		if (customerRepository.isRegistered(customer.getUsername()))
			throw new CustomerAlreadyExistsException("The username '"+ customer.getUsername() + "' already exits!");
		else {
			customerRepository.save(customer); 
		}
	}
	
	/**
	 * Login a customer specified by it's username and password
	 * @param username The username of the customer
	 * @param password The password of the customer
	 * @throws CustomerNotFoundException raised when the customer is not found in the 
	 * 		registered customers list.
	 * @throws IncorrectAuthenticationException When the provided password is incorrect.
	 */
	public CustomerEntity loginCustomer(String username, String providedPassword) 
			throws CustomerNotFoundException, IncorrectAuthenticationException {
		CustomerEntity c = getCustomerByName(username);
		String securedPassword = c.getPwd();
		String salt = c.getSalt();
		boolean passwordMatch = PasswordEncryption.verifyUserPassword(providedPassword, securedPassword, salt);
		if (!passwordMatch)
			throw new IncorrectAuthenticationException("Unauthorized login, Incorrect username or password.");
		return c;
	}
	
	/**
	 * Delete a customer from the Flopbox API
	 * @param usernameToDelete the user name of the customer to delete from the database
	 * @param customer The customer who asked the request
	 * @return returns the name of the deleted customer
	 * @throws NotFoundResourceException When the customer is not found.
	 */
	public String deleteCustomer(String usernameToDelete, CustomerEntity customer) throws NotFoundResourceException {
		if (!usernameToDelete.contentEquals(customer.getUsername()))
			throw new NotFoundResourceException();
		customerRepository.delete(customer);
		return customer.getUsername();
	}
	
}
