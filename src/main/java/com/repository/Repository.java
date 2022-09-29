package com.repository;

import java.util.HashMap;
import java.util.List;

import com.data.JsonDatabase;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

/**
 * This interface is responsible of the persistence layer. It provides
 * an interface to access the database. 
 * @author idtaleb
 */
public interface Repository {

    public void setDataWorker(JsonDatabase newdb);
}
