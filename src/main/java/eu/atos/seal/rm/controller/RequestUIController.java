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

import java.util.List;

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
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.AttributeClient;
import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.AttributeSetClient;
import eu.atos.seal.rm.model.AttributeSetList;
import eu.atos.seal.rm.model.AttributeSetClient;


@Controller
public class RequestUIController
{
	private static final Logger log = LoggerFactory.getLogger(RequestUIController.class);

	
	
    @GetMapping("request_client")
    public String getHtmlForm(HttpSession session, Model model) throws Exception
    {
        AttributeTypeList attributesRequestList = (AttributeTypeList) session
                .getAttribute("attributesRequestList");

        String urlReturn = (String) session.getAttribute("urlReturn");
        String urlFinishProcess = (String) session.getAttribute("urlFinishProcess");
        String infoMessage = (String) session.getAttribute("infoMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        String SPName = (String) session.getAttribute("SPName");
        String privacyPolicy = (String) session.getAttribute("privacyPolicy");
        String consentQuery = (String) session.getAttribute("consentQuery");
        String consentFinish = (String) session.getAttribute("consentFinish");

      //  if ( attributesRequestList == null ||   
      //  	urlReturn == null || urlFinishProcess == null)
      //  {
      //      throw new Exception("Data not initialize");
      //  }


        List<AttributeClient> attributeClientList = new ArrayList<AttributeClient>();

        for (int i = 0; i < attributesRequestList.size(); i++)
        {
            AttributeClient attributeClient = AttributeClient
                    .getAttributeClientFrom(attributesRequestList.get(i), i);
            attributeClientList.add(attributeClient);
        }
        model.addAttribute("attributesRequestList", attributeClientList);
        
        List<AttributeClient> attributeClientSendList = new ArrayList<AttributeClient>();
        model.addAttribute("attributesSendList", attributeClientSendList);  
        
        List<AttributeSetClient> consentList = new ArrayList<AttributeSetClient>();
        model.addAttribute("attributesConsentList", consentList);
        model.addAttribute("urlFinishProcess", urlFinishProcess);
        model.addAttribute("urlReturn", urlReturn);

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
        model.addAttribute("consentFinish", consentFinish);
        model.addAttribute("consentQuery", consentQuery);
        
        EntityMetadata em1 = new EntityMetadata();

        return "requestForm";
        
    }

    @GetMapping("request_client/init")
    public String initSessionParams(HttpSession session)
    {
    	AttributeTypeList attributes = new AttributeTypeList();
    	
    	// Set attribute 1

    	AttributeType attr1 = new AttributeType();	
    	attr1.setName("http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName");
    	attr1.setFriendlyName("CurrentGivenName");
    	attr1.setEncoding("plain");
    	attr1.setLanguage("ES_es");
    	attr1.setMandatory(true);
    	
    	// Set attribute2
    	AttributeType attr2 = new AttributeType();
    	attr2.setName("http://eidas.europa.eu/attributes/naturalperson/FamilyName");
    	attr2.setFriendlyName("FamilyName");
    	attr2.setEncoding("plain");
    	attr2.setLanguage("ES_es");
    	attr2.setMandatory(true);
	
     	attributes.add(attr1);
    	attributes.add(attr2);

    	// Set attribute3
    	AttributeType attr3 = new AttributeType();
    	attr3.setName("Age");
    	attr3.setFriendlyName("Age"); 

        AttributeTypeList attributeList = new AttributeTypeList();


        attributeList.add(attr1);
        attributeList.add(attr2);
        attributeList.add(attr3);
        
        
        session.setAttribute("attributesRequestList", attributeList);
        session.setAttribute("urlReturn", "response_client"); 		
        session.setAttribute("urlFinishProcess", "request_client"); 
        
        session.setAttribute("errorMessage", "An error has ocurred");
        session.setAttribute("infoMessage", "This is an information message");
        session.setAttribute("privacyPolicy", "https://project-seal.eu/privacy-policy");
        session.setAttribute("consentQuery", "Click to consent to the above data query and to receive requested data." +
                " You are accepting the privacy policy conditions, please be sure you understand them.");
        session.setAttribute("consentReturn", "Click to CONSENT to return your above data to Service Provider and finish the process." +
                " You are accepting the privacy policy conditions, please be sure you understand them."); 

        return "redirect:../request_client";
    }
    
    @PostMapping("request_client")
    public String getRequest(@RequestBody MultiValueMap<String, String> formData,
            HttpSession session, Model model)
    {
        List<String> attrRequestList = formData.get("attrRequestList");
        System.out.println("The following source has been selected"+ attrRequestList.toString());
        
        return "rm_redirection"; // Need input to decide on where to redirect from here.
    }
  
    
}
