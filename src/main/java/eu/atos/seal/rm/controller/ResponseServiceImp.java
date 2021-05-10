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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.ApiClassEnum;
import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.AttributeSet.*;
import eu.atos.seal.rm.model.AttributeSetList;
import eu.atos.seal.rm.model.AttributeSetStatus;
import eu.atos.seal.rm.model.AttributeType;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.model.LinkRequest;
import eu.atos.seal.rm.model.MsMetadata;
import eu.atos.seal.rm.model.MsMetadataList;
import eu.atos.seal.rm.model.PublishedApiType;
import eu.atos.seal.rm.service.cm.ConfMngrConnService;
import eu.atos.seal.rm.service.sm.SessionManagerConnService;
//import eu.atos.seal.rm.model.DataStoreObject;
import eu.atos.seal.rm.model.DataStoreObjectList;
import eu.atos.seal.rm.model.AttributeTypeList;
import eu.atos.seal.rm.model.DataSet;

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
		// spRequestSource: PDS, eIDAS, eduGAIN
		
		
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
				endPoint = getSpResponseEndpoint(model, msName,cmConnService);  // from spMetadata
				
				/* Testing
				msName = "SAMLms_0001";
				endPoint = "https://stork.uji.es/esmoSPms/module.php/esmo/sp/response.php/esmo";				  
				Testing*/			
			}
			model.addAttribute("sessionId", sessionId);
			
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

			//
			//  Looking for the kind of endpoint to redirect: spRequestEP
			// 
			String spRequestEP="";
			spRequestEP = (String)smConnService.readVariable(sessionId, "spRequestEP");
			log.info("spRequestEP just read: "+spRequestEP);
			
			String sIsDiscovery=(String)smConnService.readVariable(sessionId, "isDiscovery");
			log.info("sIsDiscovery just read: "+sIsDiscovery);
			
			if (sIsDiscovery == null)
			{
				String errorMsg= "isDiscovery null ";
    			log.info ("Returning error: "+errorMsg);
			
    			model.addAttribute("ErrorMessage", errorMsg);
    			return "rmError";	
			}
			
			//if (spRequestEP.contains("auth")) { //auth_request
			if (  !sIsDiscovery.equalsIgnoreCase("TRUE") ) {
				// It is an auth request.
				log.info ("It's an auth request.");
		
				// Reading "dsResponse"
				Object objDsResponse = null;
				AttributeSet dsResponse = null;	
				log.info("BEFORE dsResponse: ");
				objDsResponse = smConnService.readVariable(sessionId, "dsResponse");	
				dsResponse = (new ObjectMapper()).readValue(objDsResponse.toString(),AttributeSet.class);
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
				if (objDsMetadata != null) {
					dsMetadata = (new ObjectMapper()).readValue(objDsMetadata.toString(),EntityMetadata.class);
					log.info("dsMetadata: " + dsMetadata.toString());
				}
				else
					log.info("****NULL dsMetadata!!****");

				
				// Redirecting
				return "redirectform";				
			}
			//else if (spRequestEP.contains("data")) {// data_query
			else { // sIsDiscovery=TRUE
				// Show and confirm sending response assertions
				
				DataStoreObjectList ds = null;
				DataStoreObjectList lr = null;
				
				// Reading the dataSets from the dataStore
				Object objDatastoreDS = smConnService.readDS(sessionId, "dataSet");
				
				// Reading all the contents (dataSets and linkRequests) from the dataStore				
				Object objDatastoreLR = smConnService.readDS(sessionId, "linkRequest");
				
				if (objDatastoreDS != null) {
					ds = (new ObjectMapper()).readValue(objDatastoreDS.toString(),DataStoreObjectList.class);
					log.info("dataSets stored: " + ds.toString());
					
					if (objDatastoreLR != null) {
						lr = (new ObjectMapper()).readValue(objDatastoreLR.toString(),DataStoreObjectList.class);
						log.info("linkRequests stored: " + lr.toString());
					}
				}
				else {
					String errorMsg= "dataStore: not exist";
					log.info ("Returning error: "+errorMsg);
					
					model.addAttribute("ErrorMessage", errorMsg);
					return "rmError";				
				}
				
				// Reading the spRequestModified
				AttributeSet spRequestModified = null;
				Object objSpRequest = null;
				objSpRequest = smConnService.readVariable(sessionId, "spRequestModified");
				
				spRequestModified = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
				
				
				// Open the GUI and sending the response assertions selected by the user
				// TODO: errorMsg?
				if (ds.size() > 0) {
					String spRequestSource = "";
					spRequestSource = (String)smConnService.readVariable(sessionId, "spRequestSource");
					log.info("spRequestSource just read: "+ spRequestSource);
					
					String requestSource = "";
					switch (spRequestSource.toLowerCase()) {
						case "eidas":
							requestSource = "eIDAS authentication";
							break;
						case "edugain":
							requestSource = "eduGAIN authentication";
							break;
						case "pds":
							requestSource = "Personal Data Store";
							break;
						default:
							log.info ("BE AWARE: unknown spRequestSource ***" + spRequestSource);
					}
					return prepareAndGotoResponseUI( sessionId,  model, requestSource, spRequestModified, ds, lr, null); 
				}
				else {
					String errorMsg= "Empty dataStore!!";
					log.info ("Returning error: "+errorMsg);
					
					model.addAttribute("ErrorMessage", errorMsg);
					return "rmError";
				}
					
				
			}
//			else {
//				String errorMsg= "spRequestEP: " + spRequestEP;
//				log.info ("Returning error: "+errorMsg);
//				
//				model.addAttribute("ErrorMessage", errorMsg);
//				return "rmError";
//			}
		
		
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
	
	
	@Value("${rm.multiui.privacyPolicy}") //Defined in application.properties file
    String privacyPolicy;
	
	@Value("${rm.multiui.consentFinish}") //Defined in application.properties file
    String consentFinish;
	@Value("${rm.multiui.consentFinish0}") //Defined in application.properties file
    String consentFinish0;
	@Value("${rm.multiui.consentReturn}") //Defined in application.properties file
    String consentReturn;

	private String prepareAndGotoResponseUI( String sessionId, Model model, 
			String requestSource,
			AttributeSet spRequest,
		    DataStoreObjectList dataStoreDS,
		    DataStoreObjectList dataStoreLR,
		    String errorMessage) 

	{
		log.info("prepareAndGotoResponseUI ...sessionId: "+ sessionId);
		
		// Filling dsList 
		// and attributeSendList--> NOT NECESSARY 
//		AttributeTypeList attributesSendList = new AttributeTypeList();
		List<DataSet> dsList = new ArrayList<DataSet>();
		List<LinkRequest> lrList = new ArrayList<LinkRequest>();
		if (dataStoreDS != null && dataStoreDS.size()> 0) { // Non empty dataStoreDS
			
			dataStoreDS.forEach ((dso)-> {
				log.info("dso.toString(): " + dso.toString());
				
				DataSet aux_ds = null;
				try {
					aux_ds = (new ObjectMapper()).readValue(dso.getData(), DataSet.class);
					aux_ds.setId(dso.getId());   // Be aware of this!
					log.info("aux_ds: " + aux_ds.toString());
					dsList.add(aux_ds);
					
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
			
			if (dataStoreLR != null && dataStoreLR.size()>0) { // Non empty dataStoreLR
				
				dataStoreLR.forEach ((dso)-> {
					log.info("LR dso.toString(): " + dso.toString());
					
					LinkRequest aux_lr = null;
					try {
						aux_lr = (new ObjectMapper()).readValue(dso.getData(), LinkRequest.class);
						aux_lr.setId(dso.getId());   // Be aware of this!
						log.info("aux_lr: " + aux_lr.toString());
						lrList.add(aux_lr);
						
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				});
			}
			
		}
		
		// Filling attributesRequestList: for filtering. See bellow. 
		AttributeTypeList attributesRequestList = new AttributeTypeList();
		for ( AttributeType attrRequested : spRequest.getAttributes())
		{
			attributesRequestList.add(attrRequested);
		}
		 
		session.setAttribute("requestSource", requestSource);
		session.setAttribute("urlReturn", "response_client/return"); 		// Consenting: ACCEPT
        session.setAttribute("urlFinishProcess", "response_client/finish"); // No consenting: REJECT
        session.setAttribute("urlFinishProcess0", "response_client/back"); 	// No matching data: BACK
        
		session.setAttribute("dsList", dsList); // TO REMOVE???
		session.setAttribute("lrList", lrList); // TO REMOVE???
		
		AttributeSetList attributesConsentList = new AttributeSetList();
		
		if (dsList.size() > 0) {
			
			for (DataSet auxDs  : dsList) {
				log.info ("Filtering with the requested attributes ...");
				
				List<AttributeType> attrs = new ArrayList<AttributeType>();
				boolean found = false;
				for (AttributeType auxAttr : auxDs.getAttributes()) {
					//log.info("auxAttr friendly: " + auxAttr.getFriendlyName());
					//log.info("auxAttr: " + auxAttr.getName());
					for (AttributeType reqAttr : attributesRequestList) {
						//log.info("reqAttr friendly: " + reqAttr.getFriendlyName());
						//log.info("reqAttr: " + reqAttr.getName());
						if ((reqAttr.getFriendlyName() != null) && (reqAttr.getFriendlyName().contains(auxAttr.getFriendlyName())) || 
							reqAttr.getName().contains(auxAttr.getName())) {
							found = true;
							break;
						}	
					}
					if (found) {	
						//log.info("Found friendly: " + auxAttr.getFriendlyName());
						//log.info("Found: " + auxAttr.getName());
						attrs.add(auxAttr);	
						found = false;
					}				
				}
				if (attrs.size() != 0) {
					
					AttributeSet attributeSet = new AttributeSet();
					attributeSet.setId(auxDs.getId());   // The "external" id
//					attributeSet.setIssuer(	getIssuerIdLnk(auxDs, auxDs.getIssuerId()) + " (" + auxDs.getType() + " LoA: " + 
//											(auxDs.getLoa() != null ? auxDs.getLoa() : "unknown") + ")");  // To be shown in the response form
					
					if (!auxDs.getType().contains("derived")) { //not derived id
						attributeSet.setIssuer(	getIssuerIdLnk(auxDs, auxDs.getIssuerId()) + 
								" (Type: dataSet" + 
								" (Source: " + auxDs.getType() +
								" (LoA");
						
					}
					else {
						attributeSet.setIssuer(	getIssuerIdLnk(auxDs, auxDs.getIssuerId()) + 
								" (Type: derivedId" + 
								" (Source: SEAL derivation" +
								" (LoA");
					}
					
					
					attributeSet.setLoa(auxDs.getLoa());
					
					attributeSet.setAttributes(attrs);
					
					attributesConsentList.add(attributeSet);
				}			
			}	
			
			if (lrList.size() > 0) {
				
				for (LinkRequest auxLr  : lrList) {
					log.info ("LR****Filtering with the requested attributes ...");
					
					List<AttributeType> attrs = new ArrayList<AttributeType>();
					boolean found = false;
					
					/* Paco says: to show the uri only, not the attributes linked. 07.05.2021
					 
					for (AttributeType auxAttr : auxLr.getDatasetA().getAttributes()) {
						//log.info("LR****DATASET_A: auxAttr friendly: " + auxAttr.getFriendlyName());
						//log.info("LR****DATASET_A: auxAttr: " + auxAttr.getName());
						for (AttributeType reqAttr : attributesRequestList) {
							//log.info("reqAttr friendly: " + reqAttr.getFriendlyName());
							//log.info("reqAttr: " + reqAttr.getName());
							if ((reqAttr.getFriendlyName() != null) && (reqAttr.getFriendlyName().contains(auxAttr.getFriendlyName())) || 
								reqAttr.getName().contains(auxAttr.getName())) {
								found = true;
								break;
							}	
						}
						if (found) {	
							//log.info("LR****DATASET_A: Found friendly: " + auxAttr.getFriendlyName());
							//log.info("LR****DATASET_A: Found: " + auxAttr.getName());
							attrs.add(auxAttr);	
							found = false;
						}				
					}
					
					found = false;
					for (AttributeType auxAttr : auxLr.getDatasetB().getAttributes()) {
						//log.info("LR****DATASET_B: auxAttr friendly: " + auxAttr.getFriendlyName());
						//log.info("LR****DATASET_B: auxAttr: " + auxAttr.getName());
						for (AttributeType reqAttr : attributesRequestList) {
							//log.info("reqAttr friendly: " + reqAttr.getFriendlyName());
							//log.info("reqAttr: " + reqAttr.getName());
							if ((reqAttr.getFriendlyName() != null) && (reqAttr.getFriendlyName().contains(auxAttr.getFriendlyName())) || 
								reqAttr.getName().contains(auxAttr.getName())) {
								found = true;
								break;
							}	
						}
						if (found) {	
							//log.info("LR****DATASET_B: Found friendly: " + auxAttr.getFriendlyName());
							//log.info("LR****DATASET_B: Found: " + auxAttr.getName());
							attrs.add(auxAttr);	
							found = false;
						}				
					}
					*/
					
					for (AttributeType auxAttr : auxLr.getDatasetA().getAttributes()) {
						//log.info("LR****DATASET_A: auxAttr friendly: " + auxAttr.getFriendlyName());
						//log.info("LR****DATASET_A: auxAttr: " + auxAttr.getName());
						for (AttributeType reqAttr : attributesRequestList) {
							//log.info("reqAttr friendly: " + reqAttr.getFriendlyName());
							//log.info("reqAttr: " + reqAttr.getName());
							if ((reqAttr.getFriendlyName() != null) && (reqAttr.getFriendlyName().contains(auxAttr.getFriendlyName())) || 
								reqAttr.getName().contains(auxAttr.getName())) {
								found = true;
								break;
							}	
						}
						if (found) {	
							//log.info("LR****DATASET_A: Found friendly: " + auxAttr.getFriendlyName());
							//log.info("LR****DATASET_A: Found: " + auxAttr.getName());
							
							AttributeType lnkAttr = new AttributeType();
							lnkAttr.setName("SealLink");
							lnkAttr.setFriendlyName("SealLink");
							List<String> values = new ArrayList <String> ();
							//values.add(auxLr.getUri());
							values.add(auxLr.getId());
							lnkAttr.setValues(values);
							attrs.add(lnkAttr);
							
							break;
						}				
					}
					
					if (!found) {
						for (AttributeType auxAttr : auxLr.getDatasetB().getAttributes()) {
							//log.info("LR****DATASET_B: auxAttr friendly: " + auxAttr.getFriendlyName());
							//log.info("LR****DATASET_B: auxAttr: " + auxAttr.getName());
							for (AttributeType reqAttr : attributesRequestList) {
								//log.info("reqAttr friendly: " + reqAttr.getFriendlyName());
								//log.info("reqAttr: " + reqAttr.getName());
								if ((reqAttr.getFriendlyName() != null) && (reqAttr.getFriendlyName().contains(auxAttr.getFriendlyName())) || 
									reqAttr.getName().contains(auxAttr.getName())) {
									found = true;
									break;
								}	
							}
							if (found) {	
								//log.info("LR****DATASET_B: Found friendly: " + auxAttr.getFriendlyName());
								//log.info("LR****DATASET_B: Found: " + auxAttr.getName());
								
								AttributeType lnkAttr = new AttributeType();
								lnkAttr.setName("SealLink");
								lnkAttr.setFriendlyName("SealLink");
								List<String> values = new ArrayList <String> ();
								//values.add(auxLr.getUri());
								values.add(auxLr.getId());
								lnkAttr.setValues(values);
								attrs.add(lnkAttr);	

								break;
							}				
						}
					}
					
					if (attrs.size() != 0) {
						
						AttributeSet attributeSet = new AttributeSet();
						attributeSet.setId(auxLr.getId());
//						attributeSet.setIssuer(getIssuerIdLnk(auxLr.getDatasetA(), auxLr.getDatasetA().getIssuerId()) + " + " + 	// To be shown in the response form
//								getIssuerIdLnk(auxLr.getDatasetB(), auxLr.getDatasetB().getIssuerId()) + " (SEAL LLoA: " + 
//								(auxLr.getLloa() != null ? auxLr.getLloa() : "unknown") + ")");
						
						if ((!auxLr.getDatasetA().getType().contains ("derived")) &&
							(!auxLr.getDatasetB().getType().contains ("derived"))	) { // There is no derived id
							
							attributeSet.setIssuer(	"SEAL linking" + 
									" (Type: linkRequest" +
									" (Source: " + auxLr.getDatasetA().getType() + ", " + auxLr.getDatasetB().getType() +
									" (LLoA");
						}
						else {
							String realSource = "";
							if (!auxLr.getDatasetA().getType().contains ("derived"))
								realSource = auxLr.getDatasetA().getType();
							else
								realSource = auxLr.getDatasetB().getType();
							
							attributeSet.setIssuer(	"SEAL linking" + 
									" (Type: linkRequest" +
									" (Source: " + realSource +
									" (LLoA");
						}
						
						
						
						
						attributeSet.setLoa(auxLr.getLloa());
						
						attributeSet.setAttributes(attrs);
						
						attributesConsentList.add(attributeSet);
					}			
			
				}
			}
		
		}
		
		log.info("attributesConsentList: " + attributesConsentList);
		session.setAttribute("attributesConsentList", attributesConsentList);
		
		session.setAttribute("sessionId", sessionId);
		if(errorMessage != null)
			session.setAttribute("errorMessage", errorMessage);		
		if (privacyPolicy != null)
			session.setAttribute("privacyPolicy",privacyPolicy);
		if (consentFinish != null)
			session.setAttribute("consentFinish",consentFinish);
		if (consentFinish0 != null)
			session.setAttribute("consentFinish0",consentFinish0);
		if (consentReturn != null)
			session.setAttribute("consentReturn",consentReturn);
		
		
		model.addAttribute("dsList", dsList);
		model.addAttribute("lrList", lrList);
//		model.addAttribute("attributesRequestList", attributesRequestList);
//		model.addAttribute("attributesSendList", attributesSendList);
		model.addAttribute("attributesConsentList", attributesConsentList);
		model.addAttribute("requestSource", requestSource);
		
		if (attributesConsentList.size() > 0)
			//return "redirect:../rm/response_client"; 
			return "redirect:../response_client"; 
		else
			return "redirect:../response_client0";	// There's been no requested attribute found.
		
		//TODO Move to rest_api.controllers.client.MultiUIController***?
		// ResponseUIController.java in this package by the moment.
		// See the related responseForm.html
	}
	
	
	// ***Implementation of the ENDPOINT to be called from the form response_client!! "response_client/return"
	@Override
	public String returnFromResponseUI(String sessionId, Model model) throws Exception 
	{
		
		AttributeSetList responseAssertions= new AttributeSetList ();       
/*		List<DataSet> dsConsentList = (List<DataSet>) session.getAttribute("dsConsentList");
		log.info("dsConsentList: " + dsConsentList.toString());
		
		for (DataSet ds:dsConsentList) {
			AttributeSet consentResponse = new AttributeSet();
			consentResponse.setAttributes (ds.getAttributes());
			
			responseAssertions.add(consentResponse);			
		}

*/
		
		log.info ("attributesConsentList: " + session.getAttribute("attributesConsentList").toString());
		responseAssertions = (AttributeSetList)session.getAttribute("attributesConsentList");
		
		for (AttributeSet attr :responseAssertions ) {
			String oldIss = attr.getIssuer();
			
			if (!oldIss.contains ("SEAL linking")) { // It's not a linked id
				attr.setIssuer(oldIss.substring(0, oldIss.indexOf(" (")));
			}
			else {
				attr.setIssuer(oldIss.substring(oldIss.indexOf(" (Source: ") + " (Source: ".length(), oldIss.indexOf(" (LLoA")));				
			}
				
		}
		
		log.info ("responseAssertions just consented: " + responseAssertions.toString());
		
		ObjectMapper objMapper = new ObjectMapper();
		String endPoint = null;
		try
		{
			// Updating the responseAssertions consented by the user.
			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(responseAssertions));
			
			String spRequestEP = null;
			spRequestEP = (String)smConnService.readVariable(sessionId, "spRequestEP");
					
			if ((spRequestEP != null) && (spRequestEP.contains("data")))  // PDS
					// Clearing the authenticatedSubject 
					smConnService.updateVariable(sessionId,"authenticatedSubject",objMapper.writeValueAsString(null));
		
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
	
	@Override
	public String goToSelectIUI_2(Model model, String sessionId)
	{
		log.info("Entering goToSelectUI_2");
		
		AttributeSet spRequestModified = null;
    	EntityMetadataList sourceList = null;
    	
    	Object objSpRequest = null;
		try
		{
			objSpRequest = smConnService.readVariable(sessionId, "spRequestModified");
		
			if (objSpRequest!=null)
			{
				spRequestModified = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
				log.info("RequestAttributes: Reading spRequestModified");
			}
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData spRequest)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        //return "rmError";
	        return null;
		}
    	
		
		AttributeTypeList attributeRequestList = new AttributeTypeList();

		attributeRequestList.addAll(spRequestModified.getAttributes());
		session.setAttribute("attributesRequestList", attributeRequestList);
		session.setAttribute("sourceList",sourceList);
		session.setAttribute("urlReturn", "request_client/return");
		session.setAttribute("sessionId", sessionId);
		
		return "redirect:../request_client";
	}
	
	
	// ***Implementation of the ENDPOINT to be called from the form response_client!! "response_client/finish"
	@Override
	public String returnNothing (String sessionId, Model model) throws Exception 
	{
		ObjectMapper objMapper = new ObjectMapper();
		String endPoint = null;
		try
		{
			// Updating the responseAssertions consented by the user: none
			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(null));
		
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
		
	
	private String getIssuerIdLnk(DataSet auxDs, String issuerId) {
		  String theIssuerId = null;
		  
		  List<AttributeType> auxList = new ArrayList<AttributeType>();
		  auxList = auxDs.getAttributes();
		  
		  for (AttributeType attr: auxList) {
			  if ((attr.getFriendlyName() != null) && 
				 (attr.getFriendlyName().contains (issuerId))){
				  
				  theIssuerId = attr.getValues().get(0);
				  break;
			  }
		  }
		  
		  return (theIssuerId != null ? theIssuerId : issuerId);
		
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