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

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.EntityMetadataList;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Controller
public class ResponseApiController implements ResponseApi {

    private static final Logger log = LoggerFactory.getLogger(ResponseApiController.class);

    private final ObjectMapper objectMapper;
    
    @Autowired
    private ResponseService responseService;
    
    @Autowired
    private RequestService requestService;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ResponseApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }
    
    @GetMapping("response_client/finish")
    public String rejectConsent(HttpSession session,Model model) throws Exception
    {
    	log.info("REJECT: Entering response_client/finish ...");
    	return responseService.returnNothing ((String)session.getAttribute("sessionId"), model);
    }
    
    @GetMapping("response_client/return")
    public String acceptConsent (HttpSession session, Model model) throws Exception
    {
    	log.info("ACCEPT: Entering response_client/return ...sessionId: " + session.getAttribute("sessionId"));
    	return responseService.returnFromResponseUI ((String)session.getAttribute("sessionId"), model);
    }
    
    @GetMapping("response_client/back")
    public String backToRequest (HttpSession session, Model model) throws Exception
    {
    	String sessionId = (String) session.getAttribute("sessionId");
    	log.info("ACCEPT: Entering response_client/back ...sessionId: " + sessionId);
     	
    	return responseService.goToSelectIUI_2 (model, sessionId);
    	
    }
    

    //public ResponseEntity<Void> responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) 
    public String responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) 
    {
        log.info("responsePost called");
    	
        try {
        	
			String sReturn = responseService.rmResponse(msToken, model);
			log.info("responsePost: sReturn="+sReturn);
			
			return sReturn;
			//return new ResponseEntity<Void>(HttpStatus.OK);

			
		} 
        //catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
		//		| InvalidKeySpecException | IOException e) {
		catch (Exception e)
        {

			log.error(e.getMessage());
			
			if (e.getMessage().contains(Integer.toString(HttpStatus.BAD_REQUEST.value()))) 
	        	
				//return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
				return (Integer.toString(HttpStatus.BAD_REQUEST.value()));
        	
        	else
        		//return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        		return (Integer.toString(HttpStatus.UNAUTHORIZED.value()));
			
			
		}

        
    }

}
