package com.repository.factory;

import com.repository.CustomerRepository;
import com.repository.CustomerRepositoryImp;
import com.repository.Repository;
import com.services.Serviceable;
import com.services.customers.CustomerService;

public class CustomerRepositoryFactory implements RepositoryFactory {

	/**
	 * Use this convenience methods instead of the implemented methods.
	 * @return returns a repository factory depending of the type of the sender.
	 */
	public static CustomerRepository buildRepository() {
		return (CustomerRepository) new CustomerRepositoryFactory().createRepository();
	}

	/**
	 * Use this convenience methods instead of the implemented methods.
	 * @return returns a service factory depending of the type of the sender.
	 */
	public static CustomerService buildService() {
		return (CustomerService) new CustomerRepositoryFactory().createService();
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
