package com.repository;

import com.data.JsonDataBase;

/**
 * Implementation of the the persistence layer interface.
 * This implementation makes it possible to make the link between 
 * the sub-repositories and the database. 
 * Child classes can benefit from access to the datebase.
 * @author idtaleb
 */
public class RepositoryImp implements Repository {
	/** The link to the database */
	protected static JsonDataBase dataWorker;
	
	public RepositoryImp() {
		dataWorker = new JsonDataBase();
	}

}
