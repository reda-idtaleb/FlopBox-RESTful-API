package com.repository.factory;

import com.repository.Repository;
import com.repository.ServerRepository;
import com.repository.ServerRepositoryImp;
import com.services.Serviceable;
import com.services.servers.FTPServerService;

public class ServerRepositoryFactory implements RepositoryFactory {

	public ServerRepositoryFactory() {}

	@Override
	public Repository createRepository() {
		return new ServerRepositoryImp();
	}

	@Override
	public Serviceable createService() {
		return new FTPServerService((ServerRepository) this.createRepository());
	}

}
