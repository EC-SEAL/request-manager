package eu.atos.seal.rm.controller;
/**
 * Copyright © 2020  Atos Spain SA. All rights reserved.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import eu.atos.seal.rm.model.AttributeType;
import eu.atos.seal.rm.model.AttributeTypeList;
import eu.atos.seal.rm.model.DataSet;
import eu.atos.seal.rm.model.MsMetadata;


@Controller
public class ResponseUIController
{
	private static final Logger log = LoggerFactory.getLogger(ResponseUIController.class);
	
//    @GetMapping("idpRedirection")
//    public String getIdpRedirection(HttpSession session, Model model) throws Exception
//    {
//        System.out.println("En MultiUIController.getIdpRedirection");
//
//        return "idpRedirect";
//    }

    @GetMapping("response_client")
    public String getHtmlForm(HttpSession session, Model model) throws Exception
    {
    	//TODO
    	
        EntityMetadataList apsList = (EntityMetadataList) session.getAttribute("apsList");
        AttributeTypeList attributesRequestList = (AttributeTypeList) session
                .getAttribute("attributesRequestList");
        AttributeTypeList attributesSendList = (AttributeTypeList) session
                .getAttribute("attributesSendList");
        AttributeSetList attributesConsentList = (AttributeSetList) session
                .getAttribute("attributesConsentList");
        String urlReturn = (String) session.getAttribute("urlReturn");
        String urlFinishProcess = (String) session.getAttribute("urlFinishProcess");
        String infoMessage = (String) session.getAttribute("infoMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        String SPName = (String) session.getAttribute("SPName");
        String privacyPolicy = (String) session.getAttribute("privacyPolicy");
        String consentQuery = (String) session.getAttribute("consentQuery");
        String consentFinish = (String) session.getAttribute("consentFinish");

        if (apsList == null || attributesRequestList == null || urlReturn == null
                || urlFinishProcess == null)
        {
            throw new Exception("Data not initialize");
        }

        List<APClient> apClientList = new ArrayList<APClient>();

        for (int i = 0; i < apsList.size(); i++)
        {
            APClient apClient = APClient.getAPClientFrom(apsList.get(i), i);
            apClientList.add(apClient);
        }

        model.addAttribute("apsList", apClientList);

        List<AttributeClient> attributeClientList = new ArrayList<AttributeClient>();

        for (int i = 0; i < attributesRequestList.size(); i++)
        {
            AttributeClient attributeClient = AttributeClient
                    .getAttributeClientFrom(attributesRequestList.get(i), i);
            attributeClientList.add(attributeClient);
        }

        model.addAttribute("attributesRequestList", attributeClientList);

        List<AttributeClient> attributeClientSendList = new ArrayList<AttributeClient>();

        if (attributesSendList != null)
        {
            for (int i = 0; i < attributesSendList.size(); i++)
            {
                AttributeClient attributeClient = AttributeClient
                        .getAttributeClientFrom(attributesSendList.get(i), i);
                attributeClientSendList.add(attributeClient);
            }
        }

        model.addAttribute("attributesSendList", attributeClientSendList);

        List<AttributeSetClient> consentList = new ArrayList<AttributeSetClient>();

        if (attributesConsentList != null)
        {
            for (AttributeSet attributeSet : attributesConsentList)
            {
                AttributeSetClient attributeSetClient = new AttributeSetClient();
                attributeSetClient.setId(attributeSet.getIssuer());

                List<AttributeClient> aux = new ArrayList<AttributeClient>();
                if (attributeSet.getAttributes() != null)
                {
                    for (int i = 0; i < attributeSet.getAttributes().size(); i++)
                    {
                        AttributeClient attributeClient = AttributeClient
                                .getAttributeClientFrom(attributeSet.getAttributes().get(i), i);
                        aux.add(attributeClient);
                    }

                    attributeSetClient.setAttributeClientList(aux);
                }
                consentList.add(attributeSetClient);
            }
        }

        model.addAttribute("attributesConsentList", consentList);

        model.addAttribute("urlFinishProcess", urlFinishProcess);

        if (infoMessage != null)
        {
            model.addAttribute("infoMessage", infoMessage);
        }
        if (errorMessage != null)
        {
            model.addAttribute("errorMessage", errorMessage);
        }

        model.addAttribute("SPName", (SPName != null) ? SPName : "Service Provider");
        model.addAttribute("privacyPolicy", privacyPolicy);
        //model.addAttribute("consentQuery", consentQuery);
        model.addAttribute("consentFinish", consentFinish);

        return "responseForm";
    }

    // TESTING
    @GetMapping("response_client/init")
    public String initSessionParams(HttpSession session)
    {

// OBSOLETE this example for a dataStore
//    	{
//    		  "id": "6c0f70a8-f32b-4535-b5f6-0d596c52813a",
//    		  "encryptedData": "encryptedData",
//    		  "signature": "signature",
//    		  "signatureAlgorithm": "signatureAlgorithm",
//    		  "encryptionAlgorithm": "encryptionAlgorithm",
//    		  "clearData": [
//    		    {
//    		      "id": "6c0f70a8-f32b-4535-b5f6-0d596c52813a",
//    		      "type": "type",
//    		      "categories": [
//    		        "category1"
//    		      ],
//    		      "issuerId": "issuerId",
//    		      "subjectId": "subjectId",
//    		      "loa": "loa",
//    		      "issued": "2020-01-06T19:40:16Z",
//    		      "expiration": "2020-12-06T19:45:16Z",
//    		      "attributes": [
//    		        {
//    		          "name": "http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName",
//    		          "friendlyName": "CurrentGivenName",
//    		          "encoding": "plain",
//    		          "language": "ES_es",
//    		          "mandatory": true,
//    		          "values": [
//    		            "JOHN", "Jr"
//    		          ]
//    		        },
//		    		{
//			          "name": "http://eidas.europa.eu/attributes/naturalperson/FamilyName",
//			          "friendlyName": "FamilyName",
//			          "encoding": "plain",
//			          "language": "ES_es",
//			          "mandatory": true,
//			          "values": [
//			            "SMITH"
//			          ]
//			        }
//    		      ],
//    		      "properties": {
//    		        "additionalProp1": "prop1",
//    		        "additionalProp2": "prop2",
//    		        "additionalProp3": "prop3"
//    		      }
//    		    },
//	    {
//	      "id": "ANOTHER_6c0f70a8-f32b-4535-b5f6-0d596c52813a",
//	      "type": "ANOTHER_type",
//	      "categories": [
//	        "ANOTHER_category1"
//	      ],
//	      "issuerId": "ANOTHER_issuerId",
//	      "subjectId": "ANOTHER_subjectId",
//	      "loa": "ANOTHER_loa",
//	      "issued": "2020-01-06T19:40:16Z",
//	      "expiration": "2020-12-06T19:45:16Z",
//	      "attributes": [
//	        {
//	          "name": "http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName",
//	          "friendlyName": "CurrentGivenName",
//	          "encoding": "plain",
//	          "language": "ES_es",
//	          "mandatory": true,
//	          "values": [
//	            "JOHN", "Jr"
//	          ]
//	        }
//	      ],
//	      "properties": {
//	        "ANOTHER_additionalProp1": "ANOTHER_prop1",
//	        "ANOTHER_additionalProp2": "ANOTHER_prop2",
//	        "ANOTHER_additionalProp3": "ANOTHER_prop3"
//	      }
//	    }    	
//    		  ]
//    		}
    	
        
    	List<DataSet> dataSetList = new ArrayList<DataSet>();
    	DataSet dataSet1 = new DataSet();
    	dataSet1.setId("6c0f70a8-f32b-4535-b5f6-0d596c52813a");
    	dataSet1.setType("type");
    	dataSet1.setIssuerId("issuerId");
    	dataSet1.setSubjectId("subjectId");
    	dataSet1.setLoa("loa");
    	dataSet1.setIssued("2020-01-06T19:40:16Z");
    	dataSet1.setExpiration("2020-12-06T19:45:16Z");
    	
    	AttributeTypeList attributes = new AttributeTypeList();
    	AttributeType attr1 = new AttributeType();
    	attr1.setName("http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName");
    	attr1.setFriendlyName("CurrentGivenName");
    	attr1.setEncoding("plain");
    	attr1.setLanguage("ES_es");
    	attr1.setMandatory(true);
    	
    	List<String> values = new ArrayList<String>();
    	values.add("JOHN");
    	values.add("Jr");  	
    	attr1.setValues(values);
    	
    	AttributeType attr2 = new AttributeType();
    	attr2.setName("http://eidas.europa.eu/attributes/naturalperson/FamilyName");
    	attr2.setFriendlyName("FamilyName");
    	attr2.setEncoding("plain");
    	attr2.setLanguage("ES_es");
    	attr2.setMandatory(true);
    	
    	List<String> values2 = new ArrayList<String>();
    	values2.add("SMITH");
    	attr2.setValues(values2);
    	
     	attributes.add(attr1);
    	attributes.add(attr2);
    	dataSet1.setAttributes (attributes);
    	
    	List<String> categories = new ArrayList<String>();
    	categories.add("category1");
    	dataSet1.setCategories(categories);
    	
    	
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put ("additionalProp1", "prop1");
    	properties.put ("additionalProp2", "prop2");
    	properties.put ("additionalProp3", "prop3");
    	dataSet1.setProperties(properties);

    	dataSetList.add(dataSet1);
    	
    	DataSet dataSet2 = new DataSet();
    	dataSet2.setId("ANOTHER_6c0f70a8-f32b-4535-b5f6-0d596c52813a");
    	dataSet2.setType("ANOTHER_type");
    	dataSet2.setIssuerId("ANOTHER_issuerId");
    	dataSet2.setSubjectId("ANOTHER_subjectId");
    	dataSet2.setLoa("ANOTHER_loa");
    	dataSet2.setIssued("2020-01-06T19:40:16Z");
    	dataSet2.setExpiration("2020-12-06T19:45:16Z");
    	
    	AttributeTypeList attributes2 = new AttributeTypeList();
     	attributes2.add(attr1);
    	dataSet2.setAttributes (attributes2);
    	
    	List<String> categories2 = new ArrayList<String>();
    	categories2.add("ANOTHER_category1");
    	dataSet2.setCategories(categories2);
    	
    	
    	Map<String, String> properties2 = new HashMap<String, String>();
    	properties2.put ("ANOTHER_additionalProp1", "ANOTHER_prop1");
    	properties2.put ("ANOTHER_additionalProp2", "ANOTHER_prop2");
    	properties2.put ("ANOTHER_additionalProp3", "ANOTHER_prop3");
    	dataSet2.setProperties(properties2);

    	dataSetList.add(dataSet2);
        session.setAttribute("dataSetList", dataSetList);
        

        session.setAttribute("urlReturn", "client/return");
        session.setAttribute("urlFinishProcess", "client/finish");

        // Recolected consent attributes
        List<DataSet> dataSetConsentList = new ArrayList<DataSet>();
        dataSetConsentList.add(dataSet1);

        session.setAttribute("dataSetConsentList", dataSetConsentList);

        session.setAttribute("errorMessage", "An error has ocurred");
        session.setAttribute("infoMessage", "This is an information message");

        session.setAttribute("privacyPolicy", "https://project-seal.eu/privacy-policy");
//        session.setAttribute("consentQuery", "Click to consent to the above data query and to receive requested data." +
//                " You are accepting the privacy policy conditions, please be sure you understand them.");
        session.setAttribute("consentFinish", "Click to consent to return your above data to Service Provider and finish the process." +
                " You are accepting the privacy policy conditions, please be sure you understand them.");

        return "redirect:../response_client";
    }
    

    @PostMapping("response_client")
    public String getRequest(@RequestBody MultiValueMap<String, String> formData,
            HttpSession session, Model model)
    {
    	
    	//TODO
    	
        int apIndex = Integer.parseInt(formData.get("apId").get(0));
        String[] attrRequestList = formData.get("attrRequestList").get(0).split(",");
        String[] attrSendList = formData.get("attrSendList").get(0).split(",");
        String attrConsent = formData.get("attrConsentList").get(0);

        EntityMetadataList apsList = (EntityMetadataList) session.getAttribute("apsList");
        AttributeTypeList attributesRequestList = (AttributeTypeList) session
                .getAttribute("attributesRequestList");
        AttributeTypeList attributesSendList = (AttributeTypeList) session
                .getAttribute("attributesSendList");
        AttributeSetList attributesConsentList = (AttributeSetList) session
                .getAttribute("attributesConsentList");

        String urlReturn = (String) session.getAttribute("urlReturn");

        EntityMetadataList apsListNew = new EntityMetadataList();
        try
        {
            apsListNew.add(apsList.get(apIndex));
        }
        catch (Exception e)
        {
        }

        AttributeTypeList attributesRequestListNew = new AttributeTypeList();
        for (String index : attrRequestList)
        {
            try
            {
                attributesRequestListNew.add(attributesRequestList.get(Integer.parseInt(index)));
            }
            catch (Exception e)
            {
            }
        }

        AttributeTypeList attributesSendListNew = new AttributeTypeList();
        for (String index : attrSendList)
        {
            try
            {
                attributesSendListNew.add(attributesSendList.get(Integer.parseInt(index)));
            }
            catch (Exception e)
            {
            }
        }

        AttributeSetList attributesConsentListNew = new AttributeSetList();

        if (attrConsent != null && !attrConsent.equals(""))
        {
            String[] attributeSets = attrConsent.split("#");
            for (String attributeSet : attributeSets)
            {
                String[] aux = attributeSet.split(":");
                String id = aux[0];
                String[] indexes = aux[1].split(",");
                AttributeSet consentNew = null;

                for (AttributeSet consent : attributesConsentList)
                {
                    if (consent.getId().equals(id))
                    {
                        consentNew = consent;
                        List<AttributeType> attrs = new ArrayList<AttributeType>();
                        for (String index : indexes)
                        {
                            attrs.add(consent.getAttributes().get(Integer.parseInt(index)));
                        }
                        consentNew.setAttributes(attrs);
                        break;
                    }
                }
                if (consentNew != null)
                {
                    attributesConsentListNew.add(consentNew);
                }
            }
        }

        session.setAttribute("apsList", apsListNew);
        session.setAttribute("attributesRequestList", attributesRequestListNew);
        session.setAttribute("attributesSendList", attributesSendListNew);
        session.setAttribute("attributesConsentList", attributesConsentListNew);

        log.info ("||||| urlReturn:" + urlReturn);
        log.info("||||| sessionId:" + session.getAttribute("sessionId"));
        
        session.setAttribute("sessionId", session.getAttribute("sessionId"));
        return "redirect:" + urlReturn;
    }
}
