package com;

import static org.junit.Assert.*;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import com.exceptions.CustomerNotFoundException;
import com.exceptions.InternalServerErrorException;
import com.repository.CustomerRepositoryImp;
import com.resources.CustomerResource;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
 
public class CustomerResourceTest extends JerseyTest {
	  
	@Override
    protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(CustomerResource.class);
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
		String username = "customer";
		String loginKeys = "{\"username\":\"" +username+"\", \"pwd\":\"customer\"}";
		
	    Response response = target("/users/signin")
							.request(MediaType.APPLICATION_JSON)
	        				.post(Entity.json(loginKeys));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    CustomerRepositoryImp cusRep = new CustomerRepositoryImp();
	    try {
			cusRep.delete(cusRep.getCustomerByName(username));
		} catch (InternalServerErrorException | CustomerNotFoundException e) {
			// do nothing
		}
	}
	
	@Test
	public void testCustomerAlreadyExist() {
		String username = "customer";
		String loginKeys = "{\"username\":\"" +username+"\", \"pwd\":\"customer\"}";
		
	    Response response = target("/users/signin").request(MediaType.APPLICATION_JSON)
	        .post(Entity.json(loginKeys));
	    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
	    
	    response = target("/users/signin").request(MediaType.APPLICATION_JSON)
		        .post(Entity.json(loginKeys));
	    assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
	        
	    try {
	    	CustomerRepositoryImp cusRep = new CustomerRepositoryImp();
			cusRep.delete(cusRep.getCustomerByName(username));
		} catch (InternalServerErrorException | CustomerNotFoundException e) {
			// do nothing
		}
	}
	
	@Test
	public void testCustomerBadRequestWhenRegistering() {
		String emptyUsername = "";
		String emptyPWD = "";
		String loginKeys = "{\"username\":\"" +emptyUsername+"\", \"pwd\":\""+emptyPWD+"\"}";
		
	    Response response = target("/users/signin").request(MediaType.APPLICATION_JSON).post(Entity.json(loginKeys));
	    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	    
	    String invalidField = "other";
		loginKeys = "{\""+invalidField+"\":\"" +emptyUsername+"\", \"pwd\":\""+emptyPWD+"\"}";
		
	    response = target("/users/signin").request(MediaType.APPLICATION_JSON).post(Entity.json(loginKeys));
	    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	        
	}
	
}
