/**
Copyright © 2020  Atos Spain SA. All rights reserved.
This file is part of SEAL Request Manager (SEAL rm).
SEAL rm is free software: you can redistribute it and/or modify it under the terms of EUPL 1.2.
THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT ANY WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT, 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
See README file for the full disclaimer information and LICENSE file for full license information in the project root.

@author Atos Research and Innovation, Atos SPAIN SA
*/

package eu.atos.seal.rm.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.ApiClassEnum;
import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.AttributeSetList;
import eu.atos.seal.rm.model.AttributeSetStatus;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.MsMetadata;
import eu.atos.seal.rm.model.MsMetadataList;
import eu.atos.seal.rm.model.PublishedApiType;
import eu.atos.seal.rm.service.cm.ConfMngrConnService;
import eu.atos.seal.rm.service.sm.SessionManagerConnService;

import org.springframework.ui.Model;

@Service
public class ResponseServiceImp implements ResponseService
{
	private static final Logger log = LoggerFactory.getLogger(ResponseServiceImp.class);
	
	@Autowired
	private SessionManagerConnService smConnService;
	
	@Autowired
	private  ConfMngrConnService cmConnService;
	
	@Override
	public String rmResponse( String token, Model model) throws JsonParseException, JsonMappingException, IOException
	{
		// Check the token
		//				 
		if (token.endsWith("="))
			token = token.replace("=", "");
		if (token.startsWith("msToken="))
			token = token.replace("msToken=", "");
		
		///
		///	VALIDATE TOKEN
		///
		String sessionId="";
		try
		{
			sessionId = smConnService.validateToken(token);
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (validateToken) with token: "+token+"\n";
			errorMsg += "1 Exception message: "+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
	        log.info ("Returning error: "+errorMsg);
	        
	        return "rmError"; //TODO?
		}
		
		//  
		//	READ VARIABLE "dsResponse" 
		//  
		Object objSpRequest = null;
		AttributeSet dsResponse = null;
		try
		{
			objSpRequest = smConnService.readVariable(sessionId, "dsResponse");
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData dsResponse)  \n";
			errorMsg += "2 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.info ("Returning error: "+errorMsg);
			
	        return "rmError";
			
		}
		dsResponse = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
		log.info("dsResponse: " + dsResponse.toString() );
					
		/// 
		///	READ VARIABLE "dsMetadata" 
		///  		 
		EntityMetadata dsMetadata = null;
		Object objDsMetadata = null;
		try
		{
			objDsMetadata = smConnService.readVariable(sessionId, "dsMetadata");
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData dsMetadata)  \n";
			errorMsg += " 3 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.info ("Returning error: "+errorMsg);
			
	        return "rmError"; 
		}
		dsMetadata = (new ObjectMapper()).readValue(objDsMetadata.toString(),EntityMetadata.class);
		log.info("dsMetadata: " + dsMetadata.toString());
		
		if (dsResponse.getStatus().getCode() == AttributeSetStatus.CodeEnum.ERROR)	// [TODO] Special error?!
		{	
			//TODO ?
			log.error("dsResponse returning error");
			
			return "rmError";
//			if (dsResponse.getType() == TypeEnum.RESPONSE )
//				return manageErrorInResponse(sessionId, dsResponse, dsMetadata, model);
//			else //TypeEnum.AUTHRESPONSE
//			{
//				return manageIdpError(sessionId, dsResponse, dsMetadata, model);
//			}
		}
		
		
		AttributeSet idpResponse = new AttributeSet();
		idpResponse = dsResponse;
		
		
		// Read responseAssertions first, and then update?? TO ASK!!!
		
		AttributeSetList responseAssertions= new AttributeSetList ();
		responseAssertions.add(idpResponse);
		
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(responseAssertions));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable responseAssertions)  \n";
			errorMsg += "4 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.info ("Returning error: "+errorMsg);
			
	        return "rmError";
		}
		
		
		//  Looking for the  endpoint to redirect
		// 
		String msName = getMsName(model, sessionId, null); // Returning the FIRST ONE! ***TO ASK
		String endPoint = getSpResponseEndpoint(model, msName,cmConnService);
		if (endPoint == null  || endPoint.contains("error"))
		{
			return "rmError";
		}
			
		String tokenToSPms = "";
		try
		{
			//tokenToSPms = smConnService.generateToken(sessionId,acmMsName,"SAMLms_0001");
			tokenToSPms = smConnService.generateToken(sessionId,msName); 
		}
		catch (Exception ex)
		{
			String errorMsg= "responseAttributes: Exception calling SM (generateToken to "+msName+")  \n";
			errorMsg += "5 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.info ("Returning error: "+errorMsg);
			
	        return "rmError";
		}
		
		model.addAttribute("msToken", tokenToSPms);
		model.addAttribute("UrlToRedirect", endPoint);
		
		return "redirectform";
		//return null;
	}
	
	
	
	private EntityMetadata readSpMetadata(Model model, String sessionId)
			throws IOException, JsonParseException, JsonMappingException {
		EntityMetadata spMetadata = null;
		Object objSpMetadata = null;
		try
		{
			objSpMetadata = smConnService.readVariable(sessionId, "spMetadata");
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData spMetadata)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			model.addAttribute("ErrorMessage",errorMsg);
			log.info ("Returning error: "+errorMsg);
	        
	        //return "rmError";
		}
		spMetadata = (new ObjectMapper()).readValue(objSpMetadata.toString(),EntityMetadata.class);
		log.info("spMetadata: " + spMetadata.toString());
		
		return spMetadata;
	}
	
	public String getMsName(Model model, String sessionId,EntityMetadata spMetadata) throws IOException, JsonParseException, JsonMappingException
	//public String getMsName(Model model, String sessionId) throws IOException, JsonParseException, JsonMappingException
	{
		
		final String msName;
		
		
		//EntityMetadata spMetadata;
		if (spMetadata == null)
			spMetadata = readSpMetadata(model, sessionId);
			
		if (spMetadata.getMicroservice() == null || spMetadata.getMicroservice().size() == 0)
		{
			// ERROR
			String errorMsg= "Error getting microservice from spMetadata \n";
			
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
			
	        return "rmError"; //[TODO]
		}
		msName = spMetadata.getMicroservice().get(0);  // Choosing the first one!! TO ASK
		log.info ("spMetadata msName: "+msName);
		
		return msName;
	}
	
	public String getSpResponseEndpoint(Model model, String msName,  ConfMngrConnService cmService)
	{
		String endPoint = null;
		
		MsMetadataList spList= cmService.getMicroservicesByApiClass("SP");
		
		Optional<MsMetadata> msopt = spList.stream().filter(a->a.getMsId().equalsIgnoreCase(msName)).findAny();
		//MsMetadata ms= null;
		List<PublishedApiType> listPub;
		if (msopt.isPresent())
					listPub= msopt.get().getPublishedAPI();
		else
		{
			log.info ("Error ms not found: " + msName);
			
			return "error";//TODO
		}
		
		Optional<PublishedApiType> pubOpt = listPub.stream().filter(a->(a.getApiClass()==ApiClassEnum.SP && a.getApiCall().contains("handleResponse"))).findAny();
		if (pubOpt.isPresent())
		{
			endPoint = pubOpt.get().getApiEndpoint();
			log.info ("Endpoint: " + endPoint);
		}
		else
		{
			log.info ("Error: endpoint for *handleResponse* not found.");
			return "error";//TODO
		}
		return endPoint;
	}

}
