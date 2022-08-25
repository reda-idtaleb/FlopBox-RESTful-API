package com.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.exceptions.AliasNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

public class ServerRepositoryImp implements ServerRepository {

	public ServerRepositoryImp() {
		super();
	}

	@Override
	public FTPServerEntity getFTPServerByAlias(String alias, CustomerEntity customer) throws AliasNotFoundException {
		if (!aliasExist(alias, customer))
			throw new AliasNotFoundException(alias + " don't exist.");
		return customer.getServers().get(alias);
	}

	@Override
	public List<FTPServerEntity> findAll(CustomerEntity customer) {
		return new ArrayList<FTPServerEntity>(customer.getServers().values());
	}
	
	@Override
	public boolean aliasExist(String alias, CustomerEntity customer) {
		return customer.getServers().containsKey(alias);
	}

	@Override
	public boolean save(FTPServerEntity newServer, CustomerEntity customer) {
		try {
			customer.addServer(newServer);
			dataWorker.addCustomerData(customer);
			return true;
		} catch (InternalServerErrorException e) {
			return false;
		}
	}
	
	@Override
	public void update(CustomerEntity customer) throws InternalServerErrorException {
		dataWorker.addCustomerData(customer);
	}
	
	@Override
	public FTPServerEntity delete(String alias, CustomerEntity customer) throws AliasNotFoundException, InternalServerErrorException {
		if (!this.aliasExist(alias, customer))
			throw new AliasNotFoundException(AliasNotFoundException.getDefaultMsg(alias));
		FTPServerEntity removed = customer.getServers().remove(alias);
		this.update(customer);
		return removed;
	}
}
