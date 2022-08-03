package com.services.customers;

import java.util.HashMap;

import com.authentication.PasswordEncryption;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.repository.CustomerRepository;
import com.services.servers.FTPServerEntity;

public class CustomerEntity {
	private String username;
	private String pwd;
	private String salt;
	private HashMap<String, FTPServerEntity> servers;

	@JsonCreator
	public CustomerEntity(@JsonProperty("username") String username, @JsonProperty("pwd") String pwd) {
	     this.username = username;
	     this.salt = PasswordEncryption.getSalt(30);
	     if (pwd == null)
	    	 pwd = new String();
	     this.pwd = PasswordEncryption.generateSecurePassword(pwd, this.salt);
	     this.servers = new HashMap<String, FTPServerEntity>();
	}	
	
	/**
	 * Add a server to the customer servers list.
	 * @param server The server to add
	 */
	public void addServer(FTPServerEntity server) {
		this.servers.put(server.getAlias(), server);
	}
	
	/**
	 * @return the username of the customer
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password of the customer
	 */
	public String getPwd() {
		return pwd;
	}
	
	/**
	 * @return the salt of the customer
	 */
	public String getSalt() {
		return salt;
	}
	
	/**
	 * @return return the servers of a customer
	 */
	public HashMap<String, FTPServerEntity> getServers() {
		return servers;
	}  
	
	/**
	 * @see ServerRepository
	 */
	public FTPServerEntity getFTPServerByAlias(String alias) {
		return servers.get(alias);
	}
	
	@Override
	@JsonValue
	public String toString(){
	    return "\"" + username + "\":"+"{"
	        + "\"pwd\":" + "\"" + pwd + "\""
	        + ", \"salt\":" + "\"" + salt + "\""
	        + ", \"servers\":" + "\"" + servers + "\""
	        + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerEntity other = (CustomerEntity) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
  
}
