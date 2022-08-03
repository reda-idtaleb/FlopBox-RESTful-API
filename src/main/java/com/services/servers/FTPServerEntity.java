package com.services.servers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FTPServerEntity {
	private static final String DEFAULT_FTP_PORT = "21";
	private String alias;
	private String address;
	private String port;
	
	@JsonCreator
	public FTPServerEntity(@JsonProperty("address") String address, @JsonProperty("port") String port) {
		this.address = address;
		if (port == null)
			this.port = DEFAULT_FTP_PORT;
		else
			this.port = port;
		this.alias = "";
	}
	
	/**
	 * Copy constructor
	 * @param other The other FTPServerEntity
	 */
	public FTPServerEntity(FTPServerEntity other) {
		this.alias = other.alias;
		this.address = other.address;
		this.port = other.port;
	}

	/**
	 * @return get the alias of a server
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return get the address of the FTP server
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the ftp address to set
	 */
	public void setAddress(String url) {
		this.address = url;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		return "{\"alias\":\"" + alias + "\", \"address\":\"" + address + "\", \"port\":\"" + port
				+ "\"}";
	}

}
