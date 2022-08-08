package com;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class DummyFTPClient {
 
     private String server;
     private int port;
     private FTPClient ftpClient;
     
     public DummyFTPClient(String server, int port) {
 		this.server = server;
 		this.port = port;
 		ftpClient = new FTPClient();
 	}
     
     public void connect(String username, String password) throws IOException {
         ftpClient.connect(server, port);
         ftpClient.login(username, password);
     }
     
     public void disconnect(String filename) throws IOException {
         ftpClient.disconnect();
     }
     
     public void setServer(String server) {
         this.server = server;
     }
 
     public void setPort(int port) {
         this.port = port;
     }
	

}
