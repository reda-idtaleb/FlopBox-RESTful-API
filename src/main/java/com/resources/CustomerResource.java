package com.resources;

import com.exceptions.BadRequestException;
import com.exceptions.ConflictResourceException;
import com.exceptions.InternalServerErrorException;
import com.exceptions.NotFoundResourceException;
import com.repository.factory.CustomerRepositoryFactory;
import com.services.customers.CustomerEntity;
import com.services.customers.CustomerService;
import com.utils.AllowedDataFields;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
public class CustomerResource extends ResourceImp{
	private CustomerService customerService = ((CustomerService) new CustomerRepositoryFactory().createService());
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response welcomingToFlopBox(@Context ContainerRequestContext requestContext) {
		codeReponse = Response.Status.OK;
		reply = "Welcome to FlopBox API, please signin with " + requestContext.getUriInfo().getAbsolutePath() + "/signin";
    	return buildHTTPResponse(reply, codeReponse);
    }
	
    @Path("/signin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(CustomerEntity customer) {
    	try {
    		customerService.registerCustomer(customer);
			codeReponse = Response.Status.CREATED;
			reply = customer.getUsername()+" signedin successfully";
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		} catch (BadRequestException e) {
			codeReponse = Response.Status.BAD_REQUEST;
			reply = e.getMessage();
		} catch (InternalServerErrorException e) {
			codeReponse = Response.Status.INTERNAL_SERVER_ERROR;
			reply = e.getMessage();
		}
    	return buildHTTPResponse(reply, codeReponse);
    }

	@Path("/{name}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("name") String usernameToDelete, @Context ContainerRequestContext ctx) {
    	try {
			CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
    		String res = customerService.deleteCustomer(usernameToDelete, customer);
			codeReponse = Response.Status.OK;
			reply = res +" deleted successfully";
		} catch (NotFoundResourceException e) {
			codeReponse = Response.Status.NOT_FOUND;
			reply = e.getMessage();
		} 
    	return buildHTTPResponse(reply, codeReponse);
    }

}
