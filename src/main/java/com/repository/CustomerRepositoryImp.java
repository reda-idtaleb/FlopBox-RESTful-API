package com.repository;

import java.util.HashMap;

import com.data.JsonDatabase;
import com.exceptions.CustomerNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.services.customers.CustomerEntity;

public class CustomerRepositoryImp extends RepositoryImpl implements CustomerRepository {

	public CustomerRepositoryImp() {
		super();
	}

	@Override
	public HashMap<String, CustomerEntity> findAll() {
		return dataWorker.readJSONData();
	}

	@Override
	public CustomerEntity getCustomerByName(String username) throws CustomerNotFoundException {
		HashMap<String, CustomerEntity> registeredCustomers = findAll();
		CustomerEntity customer = registeredCustomers.get(username);
		if (customer == null)
			throw new CustomerNotFoundException("No user matches name : " + username);
		return customer;
	}

	@Override
	public boolean isRegistered(String username) {
		HashMap<String, CustomerEntity> customers = findAll();
		return customers != null && customers.containsKey(username);
	}

	@Override
	public void save(CustomerEntity customer) throws InternalServerErrorException {
		dataWorker.addCustomerData(customer);
	}
	
	@Override
	public void delete(CustomerEntity customer) throws InternalServerErrorException {
		dataWorker.deleteCustomerData(customer);
	}

}
