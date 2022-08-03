package com.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.ext.ContextResolver;

public class CustomJacksonMapperProvider implements ContextResolver<ObjectMapper> {
	
	final ObjectMapper mapper;
	
	public CustomJacksonMapperProvider() {
		mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); 
		mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

}
