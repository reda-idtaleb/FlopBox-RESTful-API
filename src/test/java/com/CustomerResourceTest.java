package com;

import static org.junit.Assert.*;

import java.util.Base64;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import com.exceptions.CustomerNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.repository.CustomerRepositoryImp;
import com.resources.CustomerResource;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
 
public class CustomerResourceTest extends JerseyTest {
	
	private static final String AUTHORIZATION = "Authorization";
	private String username, pwd;
	String loginKeys;
	
	@Override
    protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(CustomerResource.class);
    }
	
	@Before
	public void init() {
		username = "fake"; 
		pwd = "fake";
		loginKeys = String.format("{\"username\":\"%s\", \"pwd\":\"%s\"}", username, pwd);
	}
	
	@Test
	public void testWelcomingToFlopBox() {
	    Response response = target("/users").request().get();

	    assertEquals(Status.OK.getStatusCode(), response.getStatus());
	    assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

	    String content = response.readEntity(String.class);
	    assertTrue(content.contains("Welcome to FlopBox API"));
	}
	
	@Test
	public void testCreateCustomer() {
	    Response response = this.sendDummyPostRequest("/users/signin", loginKeys);
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    this.cleanResource();		
	}
	
	@Test
	public void testCustomerAlreadyExist() {
	    Response response = this.sendDummyPostRequest("/users/signin", loginKeys);
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    // Re-send the same login keys to signin
	    response = this.sendDummyPostRequest("/users/signin", loginKeys);
	    assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
	        
	    this.cleanResource();
	}
	
	@Test
	public void testCustomerBadRequestWhenRegistering() {
		String emptyUsername = "";
		String emptyPWD = "";
		
		String badLoginKeys[] = new String[] {String.format("{\"username\":\"%s\", \"pwd\":\"%s\"}", username, emptyPWD),
									 		  String.format("{\"username\":\"%s\", \"pwd\":\"%s\"}", emptyUsername, pwd),
									 		  String.format("{\"username\":\"%s\", \"pwd\":\"%s\"}", emptyUsername, emptyPWD)
											  };
		
		// When the username or/and the password are empty
		for (int i = 0; i < badLoginKeys.length; i++) {
			Response response = this.sendDummyPostRequest("/users/signin", badLoginKeys[i]);
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		}
	    
		// When an invalid field is sent 
	    String invalidField = "user";
		String badLoginKey = String.format("{\"%s\":\"%s\", \"pwd\":\"%s\"}", invalidField, username, pwd);
		Response response = this.sendDummyPostRequest("/users/signin", badLoginKey);
	    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());       
	}
	
	@Test
	public void testCustomerNotFound() {
		String serverAddress = "{\"address\":\"localhost\"}";
		Builder requestBuilder = buildRequest("myServer"); 
	    Response response = requestBuilder.post(Entity.json(serverAddress));
	    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
	
	/**
	 * Remove the dumped customer from the temporary database
	 */
	private void cleanResource() {
		try {
	    	CustomerRepositoryImp cusRep = new CustomerRepositoryImp();
			cusRep.delete(cusRep.getCustomerByName(username));
		} catch (InternalServerErrorException | CustomerNotFoundException e) {
			// do nothing
		}
	}

	private Response sendDummyPostRequest(String urlPattern, String dataAsJson) {
		return target(urlPattern)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(dataAsJson));
	}
	
	private Builder buildRequest(String alias) {
		byte[] formatLoginKeys = String.format("%s:%s", username, pwd).getBytes();
		return target("servers/"+alias)
			   .request(MediaType.APPLICATION_JSON)
			   .header(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(formatLoginKeys));
	}
	
}
