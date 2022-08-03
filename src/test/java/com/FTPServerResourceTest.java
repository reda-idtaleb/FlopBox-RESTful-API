package com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.authentication.AuthenticationFilter;
import com.exceptions.CustomerNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.repository.CustomerRepositoryImp;
import com.resources.CustomerResource;
import com.resources.FTPServerResource;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPServerEntity;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class FTPServerResourceTest extends JerseyTest {
	
	private static final String AUTHORIZATION = "Authorization";
	private CustomerEntity fakeCustomer; 
	private CustomerRepositoryImp cusRep;
	private Builder requestBuilder;
	private String serverAlias;
	
	@Override
    protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
		ResourceConfig rc = new ResourceConfig();
		rc.register(CustomerResource.class);
		rc.register(FTPServerResource.class);
        rc.register(AuthenticationFilter.class);
        rc.register(feature);
        rc.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
		return rc;
    }
	
	@Before
	public void init() {
		try {
			fakeCustomer = new CustomerEntity("fake", "fake");
			cusRep = new CustomerRepositoryImp();		
			cusRep.save(fakeCustomer);
			serverAlias = "myServer";
			requestBuilder = buildRequest(serverAlias);
		} catch (InternalServerErrorException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void free() {
		try {
			cusRep.delete(cusRep.getCustomerByName(fakeCustomer.getUsername()));
			cusRep = new CustomerRepositoryImp();	
		} catch (InternalServerErrorException | CustomerNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRegisterFTPServerOfACustomer() {
		String serverAddress = "{\"address\":\"localhost\"}";
	    Response response = requestBuilder.post(Entity.json(serverAddress));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testShowAllServers() {
		String serverAddress = "{\"address\":\"localhost\"}";
	    Response response = requestBuilder.post(Entity.json(serverAddress));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    this.requestBuilder = buildRequest("");
	    GenericType<HashMap<String, List<FTPServerEntity>>> genType = new GenericType< HashMap<String, List<FTPServerEntity>>>() {};
	    HashMap<String, List<FTPServerEntity>> servers = requestBuilder.get(genType);
	    String okReply = Response.Status.OK.getStatusCode()+"-OK";
	    assertTrue(servers.containsKey(okReply));
	    assertTrue(servers.get(okReply).size()==1);
	    assertEquals(servers.get(okReply).get(0).getAlias(), serverAlias);
	}
	
	@Test
	public void testDeleteServer() {
		String serverAddress = "{\"address\":\"localhost\"}";
	    Response response = requestBuilder.post(Entity.json(serverAddress));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    this.requestBuilder = target("servers/"+serverAlias)
				   			  .request()
				              .header(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("fake:fake".getBytes()));
	    GenericType<HashMap<String, HashMap<String, FTPServerEntity>>> genType = new GenericType<HashMap<String, HashMap<String, FTPServerEntity>>>() {};
	    HashMap<String, HashMap<String, FTPServerEntity>> servers = requestBuilder.delete(genType);
	    String okReply = Response.Status.OK.getStatusCode()+"-OK";
	    assertFalse(!servers.containsKey(okReply));
	    assertEquals(servers.get(okReply).get("deletedServer").getAlias(), serverAlias);
	}
	
	@Test
	public void testUpdateServer() {
		String serverAddress = "{\"address\":\"localhost\"}";
	    Response response = requestBuilder.post(Entity.json(serverAddress));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    String newAddress = "127.0.0.1";
	    String newServer = "{\"address\":\""+ newAddress + "\"}";
	    this.requestBuilder = target("servers/"+serverAlias)
				   			  .request()
				              .header(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("fake:fake".getBytes()));
	    GenericType<HashMap<String, HashMap<String, FTPServerEntity>>> genType = new GenericType<HashMap<String, HashMap<String, FTPServerEntity>>>() {};
	    Response reply = requestBuilder.put(Entity.json(newServer));
	    assertEquals(Response.Status.OK.getStatusCode(), reply.getStatus());
	}
	
	private Builder buildRequest(String alias) {
		return target("servers/"+alias)
			   .request(MediaType.APPLICATION_JSON)
			   .header(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("fake:fake".getBytes()));
	}

}
