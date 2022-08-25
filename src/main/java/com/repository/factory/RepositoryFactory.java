package com.repository.factory;

import com.repository.Repository;
import com.services.Serviceable;

public interface RepositoryFactory {
	
	/**
	 * @return returns a repository factory depending of the type of the sender.
	 */
	Repository createRepository();
	
	/**
	 * @return returns a service factory depending of the type of the sender.
	 */
	Serviceable createService();
	
}
