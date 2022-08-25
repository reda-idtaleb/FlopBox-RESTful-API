package com.repository;

import java.util.HashMap;
import java.util.List;

import com.data.JsonDataBase;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

/**
 * This interface is responsible of the persistence layer. It provides
 * an interface to access the database. 
 * @author idtaleb
 */
public interface Repository {
    /** The link to the database */
	static JsonDataBase dataWorker = new JsonDataBase();;
}
