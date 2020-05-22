package eu.atos.seal.rm.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.ui.Model;

public interface ResponseService
{
	public String rmResponse( String token, Model model) throws JsonParseException, JsonMappingException, IOException;
}
