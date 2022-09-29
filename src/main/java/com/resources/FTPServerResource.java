package com.resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.exceptions.BadRequestException;
import com.exceptions.ConflictResourceException;
import com.exceptions.NotFoundResourceException;
import com.services.customers.CustomerEntity;
import com.services.ftp_files.AbstractFTPFileComponentEntity;
import com.services.servers.FTPServerEntity;
import com.services.servers.FTPServerService;
import com.utils.AllowedDataFields;
import com.utils.factory.ServerBusinessFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/servers")
public class FTPServerResource extends ResourceImp{
	private FTPServerService serverService;
	
	public FTPServerResource() {
		this(ServerBusinessFactory.buildService());
	}

	public FTPServerResource(FTPServerService serverService) {
		this.serverService = serverService;
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showAllCreatedServers(@Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		List<FTPServerEntity> reply = serverService.showAllServers(customer);
		codeReponse = Response.Status.OK;
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/{alias}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showServer(@PathParam("alias") String alias, @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = serverService.showServer(alias, customer);
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;	
		}	
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/{alias}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerServer(@PathParam(AllowedDataFields.ALIAS_FIELD) String alias, 
    		FTPServerEntity server, @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 	
		server.setAlias(alias);
		try {
			reply = serverService.createServer(server, customer);
			codeReponse = Response.Status.CREATED;	
		} catch (BadRequestException e) {
			codeReponse = Response.Status.BAD_REQUEST;
			reply = e.getMessage();
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
    
	@Path("/{alias}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteServer(@PathParam(AllowedDataFields.ALIAS_FIELD) String alias, @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = serverService.deleteServer(alias, customer);
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			codeReponse = Response.Status.BAD_REQUEST;
			reply = e.getMessage();
		} 		
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/{alias}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response updateServer(@PathParam(AllowedDataFields.ALIAS_FIELD) String oldAlias, 
    				HashMap<String, String> newData, @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = serverService.updateServer(oldAlias, newData, customer);
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			codeReponse = Response.Status.NOT_FOUND;
			reply = e.getMessage();
		} catch (BadRequestException e) {
			codeReponse = Response.Status.BAD_REQUEST;
			reply = e.getMessage();
		} 
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/{alias}/files")
    public FTPFileResource getFileResource(@Context UriInfo uri) {
		String uriPath = uri.getBaseUri().resolve(uri.getPath(false)).toString();
		List<String> split =  Arrays.asList(uriPath.split("/"));
		int index = split.indexOf("files");
		split = split.subList(0, index+1);
		uriPath = String.join("/", split);
		AbstractFTPFileComponentEntity.setUri(uriPath);
        return new FTPFileResource();
    }

	public void setServerService(FTPServerService serverService) {
		this.serverService = serverService;
	}
	
}
