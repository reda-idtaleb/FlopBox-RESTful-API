package com.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.exceptions.BadRequestException;
import com.exceptions.ConflictResourceException;
import com.exceptions.NotFoundResourceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.repository.ServerRepository;
import com.repository.factory.ServerRepositoryFactory;
import com.services.customers.CustomerEntity;
import com.services.servers.FTPFileService;
import com.utils.AllowedDataFields;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Class representing a FTP file resource. The resource is secured, 
 * only the registered customers can access this resource.
 * @author idtaleb
 */
public class FTPFileResource extends ResourceImp {
	
	private FTPFileService ftpFileService = new FTPFileService(ServerRepositoryFactory.buildRepository());
	
	@Path("/list{path: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showAllFiles(@PathParam("alias") String alias, @PathParam("path") String path, 
    							 @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    							 @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    							 @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = ftpFileService.listAllFiles(alias, path, user, pwd, customer);
			codeReponse = Response.Status.OK;		
		} catch (NotFoundResourceException e) {
			codeReponse = Response.Status.NOT_FOUND;
			reply = e.getMessage();
		} catch (IOException e) {
			codeReponse = Response.Status.BAD_GATEWAY;
			reply = e.getMessage();
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT; 
			reply = e.getMessage(); 
		}
		JsonNode node = mapper.valueToTree(reply);
		json.set(buildStatus(codeReponse), node);
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/getFile/{path: .*}")
    @GET
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    public Response downloadFile(@PathParam("alias") String alias, @PathParam("path") String path, 
    							 @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    							 @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    							 @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			InputStream file = ftpFileService.downloadFile(alias, path, user, pwd, customer);
			reply = (Object) file;
			codeReponse = Response.Status.OK;
			isJsonType = false; 
		} catch (FileNotFoundException | NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/getDir/{path: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadDirectory(@PathParam("alias") String alias, @PathParam("path") String path, 
    							 @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    							 @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    							 @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = ftpFileService.downloadDirectory(alias, path, user, pwd, customer);
			codeReponse = Response.Status.OK;
		} catch (FileNotFoundException | NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/rename{path: .*}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response renameFile(@PathParam("alias") String alias, @PathParam("path") String path, 
    						   HashMap<String, String> renameData,
    						   @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    						   @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    						   @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			ftpFileService.renameFile(alias, path, renameData, user, pwd, customer);
			reply = "The file " + path + " renamed to " + renameData.get(AllowedDataFields.RENAME_FILE_FIELD) + " successfully" ;
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (BadRequestException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_REQUEST;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/delete{path: .*}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDir(@PathParam("alias") String alias, @PathParam("path") String path, 
    						   @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    						   @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    						   @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			ftpFileService.deleteDirectory(alias, path, user, pwd, customer);
			reply = "The file " + path + " deleted successfully" ;
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (BadRequestException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_REQUEST;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/mkdir{path: .*}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response createDir(@PathParam("alias") String alias, @PathParam("path") String path, 
    						   HashMap<String, String> newDirData,
    						   @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    						   @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    						   @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			ftpFileService.createDirectory(alias, path, newDirData, user, pwd, customer);
			String dirPath = path.charAt(path.length()-1) == '/' 
								? path+newDirData.get(AllowedDataFields.CREATE_DIR_FIELD) 
								: path+"/"+newDirData.get(AllowedDataFields.CREATE_DIR_FIELD) ;
			reply =  dirPath + " directory created successfully" ;
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (BadRequestException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_REQUEST;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/storeFile{path: .*}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("alias") String alias, @PathParam("path") String path, 
    						   @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    						   @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    						   @FormDataParam("file") InputStream file,
    						   @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			ftpFileService.storeFile(alias, path, file, user, pwd, customer);
			reply = "File uploaded in "+ path +" successfully" ;
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/storeDir{path: .*}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDir(@PathParam("alias") String alias, @PathParam("path") String path, 
    						   @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    						   @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    						   @FormDataParam("file") InputStream file,
    						   @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			ftpFileService.uploadDir(alias, path, user, pwd, file, customer);
			reply = "Directory uploaded in " + path + " successfully" ;
			codeReponse = Response.Status.OK;
		} catch (NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }
	
	@Path("/time{path: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModifiedTime(@PathParam("alias") String alias, @PathParam("path") String path, 
    							 @HeaderParam(AllowedDataFields.FTP_USERNAME_FIELD) String user,
    							 @HeaderParam(AllowedDataFields.PASSWORD_FIELD) String pwd,
    							 @Context ContainerRequestContext ctx) {
		CustomerEntity customer = (CustomerEntity) ctx.getProperty(CUSTOMER_PROPERTY); 
		try {
			reply = ftpFileService.getModificationTime(customer, alias, user, pwd, path);
			codeReponse = Response.Status.OK;
		} catch (FileNotFoundException | NotFoundResourceException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.NOT_FOUND;
		} catch (IOException e) {
			reply = e.getMessage();
			codeReponse = Response.Status.BAD_GATEWAY;
		} catch (ConflictResourceException e) {
			codeReponse = Response.Status.CONFLICT;
			reply = e.getMessage();
		}
		return buildHTTPResponse(reply, codeReponse);
    }

}
