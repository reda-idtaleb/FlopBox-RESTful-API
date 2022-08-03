package com.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

public class ResourceImp implements Resource{
	private static final String SEPARATOR = "-";
	protected static final String CUSTOMER_PROPERTY = "customer"; 
	
	protected ObjectMapper mapper = new ObjectMapper();
	protected ObjectNode json = mapper.createObjectNode();
	
	protected Object reply;
	protected Status codeReponse;
	protected ResponseBuilder response;
	protected boolean isJsonType = true;
	
	@Override
	public String buildStatus(Status codeReponse) {
		return String.valueOf(codeReponse.getStatusCode()) + SEPARATOR + codeReponse.getReasonPhrase();
	}
    
	@Override
	public Response buildHTTPResponse(Object reply, Status codeReponse)  {
		if (isJsonType ) {
			JsonNode node = mapper.valueToTree(reply);
			json.set(buildStatus(codeReponse), node); 
			this.response = Response.status(codeReponse).entity(json);
		} else {
			this.response = Response.ok(reply);
			this.response.status(codeReponse);		
		}
		return response.build();	
	}
}
