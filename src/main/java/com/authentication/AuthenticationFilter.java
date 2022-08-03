package com.authentication;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

import com.exceptions.CustomerNotFoundException;
import com.exceptions.IncorrectAuthenticationException;
import com.exceptions.UnauthorizedUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.repository.factory.CustomerRepositoryFactory;
import com.services.customers.CustomerEntity;
import com.services.customers.CustomerService;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
	private static final String CUSTOMER_PROPERTY = "customer";
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	private static final String SECURED_URL_PREFIX = "(users\\/\\b(?!signin\\b)\\w+)|(\\b(servers\\b).*)";
	private static final String SIGNIN_URL = "users";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (requestContext.getUriInfo().getPath().matches(SECURED_URL_PREFIX)) {
			List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
			if (authHeader != null && authHeader.size() > 0 ) {
				String authToken = authHeader.get(0);
				authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
				String decodedString = new String(Base64.getDecoder().decode(authToken));
				StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
				String username = tokenizer.nextToken();
				String password = tokenizer.nextToken();	
				try {
					CustomerEntity customer = ((CustomerService) new CustomerRepositoryFactory().createService()).loginCustomer(username, password);
					requestContext.setProperty(CUSTOMER_PROPERTY, customer);
					return ;
				} catch (UnauthorizedUserException e) {
					sendUnauthorizedMsg(requestContext, e.getMessage());
				}		
			}
			else 
				sendUnauthorizedMsg(requestContext, "Unauthorized user, cannot access the resource.");
		}
		else if (requestContext.getUriInfo().getPath().contains(SIGNIN_URL))
			return;
		else
			sendUnauthorizedMsg(requestContext, "Unauthorized user, cannot access the resource.");
	}
	
	private void sendUnauthorizedMsg(ContainerRequestContext requestContext, String msg) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		json.put("error", msg);
		Response unothorizedStatus = Response
				.status(Response.Status.UNAUTHORIZED)
				.entity(json)
				.build();
		requestContext.abortWith(unothorizedStatus);
	}

}
