package com.repository.factory;

import com.repository.CustomerRepository;
import com.repository.CustomerRepositoryImp;
import com.repository.Repository;
import com.services.Serviceable;
import com.services.customers.CustomerService;

public class CustomerRepositoryFactory implements RepositoryFactory {

	public CustomerRepositoryFactory() {
	}

	@Override
	public Repository createRepository() {
		return new CustomerRepositoryImp();
	}

	@Override
	public Serviceable createService() {
		return new CustomerService((CustomerRepository) this.createRepository());
	}

}
