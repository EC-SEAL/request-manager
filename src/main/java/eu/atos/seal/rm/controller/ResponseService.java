package eu.atos.seal.rm.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ResponseService
{
	public String rmResponse(String token) throws JsonParseException, JsonMappingException, IOException;
}
