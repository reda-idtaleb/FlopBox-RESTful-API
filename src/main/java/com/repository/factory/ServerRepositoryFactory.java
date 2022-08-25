package com.repository.factory;

import com.repository.Repository;
import com.repository.ServerRepository;
import com.repository.ServerRepositoryImp;
import com.services.Serviceable;
import com.services.servers.FTPServerService;

public class ServerRepositoryFactory implements RepositoryFactory {

	/**
	 * Use this convenience methods instead of the implemented methods.
	 * @return returns a repository factory depending of the type of the sender.
	 */
	public static ServerRepository buildRepository() {
		return (ServerRepository) new ServerRepositoryFactory().createRepository();
	}

	/**
	 * Use this convenience methods instead of the implemented methods.
	 * @return returns a service factory depending of the type of the sender.
	 */
	public static FTPServerService buildService() {
		return (FTPServerService) new ServerRepositoryFactory().createService();
	}

	@Override
	public Repository createRepository() {
		return new ServerRepositoryImp();
	}

	@Override
	public Serviceable createService() {
		return new FTPServerService((ServerRepository) this.createRepository());
	}

}
