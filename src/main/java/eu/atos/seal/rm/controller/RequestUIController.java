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
import eu.atos.seal.rm.model.AttributeClient;
import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.AttributeSetClient;
import eu.atos.seal.rm.model.AttributeSetList;


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
        String consentReturn = (String) session.getAttribute("consentReturn");

        if ( attributesRequestList == null ||   
        	urlReturn == null || urlFinishProcess == null)
        {
            throw new Exception("Data not initialize");
        }



        List<AttributeClient> attributeClientList = new ArrayList<AttributeClient>();

        for (int i = 0; i < attributesRequestList.size(); i++)
        {
            AttributeClient attributeClient = AttributeClient
                    .getAttributeClientFrom(attributesRequestList.get(i), i);
            attributeClientList.add(attributeClient);
        }

        model.addAttribute("attributesRequestList", attributeClientList);// Necessary in order to filter the results to be shown
        
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
        

        return "requestForm";
        
    }

    // TESTING
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
    	attr3.setValues(Arrays.asList("25"));

        AttributeTypeList attributeList = new AttributeTypeList();

        attributeList.add(attr1);
        attributeList.add(attr2);
        attributeList.add(attr3);
        session.setAttribute("attributesRequestList", attributeList);

        AttributeTypeList attributeSendList = new AttributeTypeList();

        attributeSendList.add(attr1);
        attributeSendList.add(attr2);
        attributeSendList.add(attr3);
        session.setAttribute("attributesSendList", attributeSendList);

        session.setAttribute("urlReturn", "response_client/return"); 		
        session.setAttribute("urlFinishProcess", "response_client/finish"); 
        
        AttributeSetList consentList = new AttributeSetList();
        List<AttributeType> attributeConsentList2 = new ArrayList<AttributeType>();
        AttributeType attrCons4 = new AttributeType();
        attrCons4.setName("course");
        attrCons4.setValues(Arrays.asList("2"));
        attributeConsentList2.add(attrCons4);

        session.setAttribute("attributesConsentList", consentList);

        session.setAttribute("errorMessage", "An error has ocurred");
        session.setAttribute("infoMessage", "This is an information message");

        session.setAttribute("privacyPolicy", "https://project-seal.eu/privacy-policy");
        session.setAttribute("consentQuery", "Click to consent to the above data query and to receive requested data." +
                " You are accepting the privacy policy conditions, please be sure you understand them.");
        //session.setAttribute("consentFinish", "Click to CANCEL the submission of your above data to Service Provider and finish the process.");
        session.setAttribute("consentReturn", "Click to CONSENT to return your above data to Service Provider and finish the process." +
                " You are accepting the privacy policy conditions, please be sure you understand them."); 

        return "redirect:../request_client";
    }
    

 
    @PostMapping("request_client")
    public String getRequest(@RequestBody MultiValueMap<String, String> formData,
            HttpSession session, Model model)
    {
    	
    	
        String[] attrRequestList = formData.get("attrRequestList").get(0).split(",");
        String[] attrSendList = formData.get("attrSendList").get(0).split(",");
        String attrConsent = formData.get("attrConsentList").get(0);
        

        List<DataSet> dsList = (List<DataSet>) session.getAttribute("dsList");
        AttributeTypeList attributesRequestList = (AttributeTypeList) session
                .getAttribute("attributesRequestList");
        AttributeTypeList attributesSendList = (AttributeTypeList) session
                .getAttribute("attributesSendList");
        AttributeSetList attributesConsentList = (AttributeSetList) session
                .getAttribute("attributesConsentList");

        String urlReturn = (String) session.getAttribute("urlReturn");


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
                    if (consent.getIssuer().equals(id))
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

        session.setAttribute("attributesRequestList", attributesRequestListNew);

        log.info("||||| attributesConsentListNew: " + attributesConsentListNew.toString());
        log.info ("||||| urlReturn:" + urlReturn);
        log.info("||||| sessionId:" + session.getAttribute("sessionId"));
        
        session.setAttribute("sessionId", session.getAttribute("sessionId"));
        return "redirect:" + urlReturn;
        
        
    }
  
    
}
