package com.resources;

import com.exceptions.InternalServerErrorException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * The responsibility of this interface is to handle client requests sent over HTTP. 
 * This layer contains Controllers, which receive requests from clients, 
 * forward them to the Service layer, and return the results to clients.
 * @author idtaleb
 */
public interface Resource {
	
	/**
	 * Build the status of the response
	 * @param codeReponse The HTTP status
	 * @return The HTTP response status
	 */
	String buildStatus(Status codeReponse);
	
	/**
	 * Build the HTTP response to send to the client
	 * @param reply The reply specifying a message
	 * @param codeReponse The HTTP code 
	 * @param isJsonContent true if the response should be a JSON format.
	 * @return The HTTP response including the code and a specified message.
	 * @throws InternalServerErrorException When 
	 */
	Response buildHTTPResponse(Object reply, Status codeReponse);
}
