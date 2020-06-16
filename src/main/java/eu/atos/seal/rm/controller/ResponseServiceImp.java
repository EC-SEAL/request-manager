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
import java.util.UUID;

import javax.servlet.http.HttpSession;

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
import eu.atos.seal.rm.model.DataStore;
import eu.atos.seal.rm.model.AttributeTypeList;

import org.springframework.ui.Model;

@Service
public class ResponseServiceImp implements ResponseService
{
	private static final Logger log = LoggerFactory.getLogger(ResponseServiceImp.class);
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private SessionManagerConnService smConnService;
	
	@Autowired
	private  ConfMngrConnService cmConnService;
	
	@Override
	public String rmResponse( String token, Model model) throws JsonParseException, JsonMappingException, IOException
	{
		//UC 8.01, UC 8.03
		
		// spRequestEP: Null, auth_request, data_query
		// NOT used *** spRequestSource: Discovery, PDS, SSI, eIDAS, eduGAIN
		
		
		// ***
		// *** SPms is going to read responseAssertions ***
		// ***
					
		
		// Check the token
		//				 
		if (token.endsWith("="))
			token = token.replace("=", "");
		if (token.startsWith("msToken="))
			token = token.replace("msToken=", "");

		String sessionId="";
		String endPoint = null;
		String msName = null;
		try
		{
			// Validate token
			sessionId = smConnService.validateToken(token);
			if (sessionId != null) {
				msName = getMsName(model, sessionId, null); // Returning the FIRST ONE! ***
				endPoint = getSpResponseEndpoint(model, msName,cmConnService);
			}
			
			log.info ("UrlToRedirect: " + endPoint);
			if (endPoint == null  || endPoint.contains("error"))
			{
				model.addAttribute("ErrorMessage", "SP endpoint not found");
				return "fatalError"; // impossible to know the endPoint...
			}
			// EndPoint to redirect
			model.addAttribute("UrlToRedirect", endPoint);
	        
			String tokenToSPms = "";		
			tokenToSPms = smConnService.generateToken(sessionId,msName); 
		
			// msToken just generated
			model.addAttribute("msToken", tokenToSPms);
		
			// Reading "dsResponse"
			Object objSpRequest = null;
			AttributeSet dsResponse = null;		
			objSpRequest = smConnService.readVariable(sessionId, "dsResponse");	
			dsResponse = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
			log.info("dsResponse: " + dsResponse.toString() );
			
			// Building responseAssertions 
			AttributeSetList responseAssertions= new AttributeSetList ();
			
	// TO REMOVE:
	//		AttributeSet idpResponse = new AttributeSet();
	//		idpResponse = dsResponse;
	//		responseAssertions.add(idpResponse);
			
			responseAssertions.add(dsResponse);
			ObjectMapper objMapper = new ObjectMapper();
			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(responseAssertions));
			
			if (dsResponse.getStatus().getCode() == AttributeSetStatus.CodeEnum.ERROR)	// Returning error to the SPms
			{	
				log.error("dsResponse returning error");
				
				model.addAttribute("ErrorMessage", "dsResponse returning error");
				return "rmError";
			}

			
			// Reading "dsMetadata"... what for???
			EntityMetadata dsMetadata = null;
			Object objDsMetadata = null;
			objDsMetadata = smConnService.readVariable(sessionId, "dsMetadata");
			dsMetadata = (new ObjectMapper()).readValue(objDsMetadata.toString(),EntityMetadata.class);
			log.info("dsMetadata: " + dsMetadata.toString());
		
					
			//
			//  Looking for the kind of endpoint to redirect: spRequestEP
			// 
			String spRequestEP="";
			spRequestEP = (String)smConnService.readVariable(sessionId, "spRequestEP");
			log.info("spRequestEP just read: "+spRequestEP);
	
				
			// TESTING:
			//spRequestEP = "testing";
			//spRequestEP = "data_query";
			//log.info("*** TESTING spRequestEP: "+spRequestEP);
			
			if (spRequestEP.contains("auth")) { //auth_request
				// Do nothing
				log.info ("It's an auth request.");
	
				// Redirecting
				return "redirectform";				
			}
			else if (spRequestEP.contains("data")) {// data_query
				// Show and confirm sending response assertions
				
				// Reading the dataStore
				DataStore dataStore = null;
				Object objDatastore = null;
					objDatastore = smConnService.readVariable(sessionId, "dataStore");
				
				// TESTING:
				log.info("*** Testing: invented dataStore");
				DataStore datastore = new DataStore();
				datastore.setId("DS_" + UUID.randomUUID().toString());
				datastore.setEncryptedData(null);
				datastore.setEncryptionAlgorithm("this is the encryption algorithm");
				datastore.setSignature("this is the signature");
				datastore.setSignatureAlgorithm("this is the signature algorithm");	
				
				datastore.setClearData(null);
				// END TESTING
				
				/* TO BE UNCOMMENTED*****
				if (objDatastore != null) {
					dataStore = (new ObjectMapper()).readValue(objDatastore.toString(),DataStore.class);
					log.info("dataStore: " + dataStore.toString());
				}
				else {
					String errorMsg= "dataStore: not exist";
					log.info ("Returning error: "+errorMsg);
					
					model.addAttribute("ErrorMessage", errorMsg);
					return "rmError";				
				}
				TO BE UNCOMMENTED*****/
				
				
				// Open the GUI and sending the response assertions selected by the user
				//return prepareAndGotoResponseUI( sessionId,  model, dataStore, null); // TODO: errorMsg?
				return prepareAndGotoResponseUI( sessionId,  model, datastore, null); 
				
			}
			else {
				String errorMsg= "spRequestEP: " + spRequestEP;
				log.info ("Returning error: "+errorMsg);
				
				model.addAttribute("ErrorMessage", errorMsg);
				return "rmError";
			}
		
		
		}
		catch (Exception ex)
		{
			String errorMsg= ex.getMessage()+"\n";
			log.info ("Returning error: "+errorMsg);
	        
	        model.addAttribute("ErrorMessage", errorMsg);
	        if (endPoint != null) 
	        	return "rmError"; 
	        else
	        	return "fatalError"; // Unknown endPoint...
		}
		
	}
	
	
	
	private String prepareAndGotoResponseUI( String sessionId, Model model, 
		      DataStore dataStore,
		      String errorMessage) 

	{
		// TODO
		log.info("TODO prepareAndGotoResponseUI");
		
		return "redirect:../rm/response_client"; 
		//TODO Move to rest_api.controllers.client.MultiUIController***?
		// ResponseUIController.java in this package by the moment.
		// See the related responseForm.html
	}
	
	
	// ***Implementation of the ENDPOINT to be called from the form response_client!! /client/finishConsent
	@Override
	public String returnFromResponseUI(String sessionId, Model model) throws Exception 
	{
		//TODO
		
		AttributeSetList responseAssertions= new AttributeSetList ();
		// ** Expecting an @PostMapping ("response_client") in MultiUIController
		AttributeTypeList attributesSendList = (AttributeTypeList) session.getAttribute("attributesConsentList");  //
		AttributeSet consentResponse = new AttributeSet();
		
		responseAssertions.add(consentResponse);
		log.info ("responseAssertions just consented: " + responseAssertions.toString());
		
		ObjectMapper objMapper = new ObjectMapper();
		String endPoint = null;
		try
		{
			// Updating the responseAssertions consented by the user.
			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(responseAssertions));
		
			String msName = getMsName(model, sessionId, null); // Returning the FIRST ONE! ***
			endPoint = getSpResponseEndpoint(model, msName,cmConnService);
			log.info ("UrlToRedirect: " + endPoint);
			if (endPoint == null  || endPoint.contains("error"))
			{
				model.addAttribute("ErrorMessage","SP endpoint not found");
				return "fatalError";
			}
				
			String tokenToSPms = "";
			tokenToSPms = smConnService.generateToken(sessionId,msName); 
		
			model.addAttribute("msToken", tokenToSPms);
			model.addAttribute("UrlToRedirect", endPoint);
			
			return "redirectform";
		
		}
		catch (Exception ex)
		{
			String errorMsg= ex.getMessage()+"\n";
			log.info ("Returning error: "+errorMsg);
			
			model.addAttribute("ErrorMessage",errorMsg);
			if (endPoint != null) 
	        	return "rmError"; 
	        else
	        	return "fatalError"; // Unknown endPoint...
		}
	}
	
	
	
	
	// TO BE REFACTORED:
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
	
	// TO BE REFACTORED:
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
			log.error(errorMsg);
			
			model.addAttribute("ErrorMessage",errorMsg);
	        return "fatalError"; 
		}
		msName = spMetadata.getMicroservice().get(0);  // Choosing the first one!! TO ASK
		log.info ("spMetadata msName: "+msName);
		
		return msName;
	}
	
	// TO BE REFACTORED:
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
		
		// sp/response *** TO UPDATE
		Optional<PublishedApiType> pubOpt = listPub.stream().filter(a->(a.getApiClass()==ApiClassEnum.SP && a.getApiCall().contains("handleResponse"))).findAny();
		if (pubOpt.isPresent())
		{
			endPoint = pubOpt.get().getApiEndpoint();
			//log.info ("Endpoint: " + endPoint);
		}
		else
		{
			log.info ("Error: endpoint for *handleResponse* not found.");
			return "error";//TODO
		}
		return endPoint;
	}

}
