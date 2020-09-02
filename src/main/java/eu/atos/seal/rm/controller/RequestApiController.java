package eu.atos.seal.rm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.service.sm.SessionManagerConnService;
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
import org.springframework.web.bind.annotation.PostMapping;
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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-11-07T15:11:31.760Z")

@Controller
public class RequestApiController implements RequestApi 
{

    private static final Logger log = LoggerFactory.getLogger(RequestApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
	private SessionManagerConnService smConn;

    @org.springframework.beans.factory.annotation.Autowired
    public RequestApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    //public ResponseEntity<Void> requestPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) {
    public String requestPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) {
    	log.debug("requestPost called");
    	System.out.println("requestPost called");
    	String accept = request.getHeader("Accept");
        
        try {
        	
			String sReturn = requestService.rmRequest(msToken, model);
			System.out.println("requestPost: sReturn="+sReturn);
			log.debug("requestPost: sReturn="+sReturn);
			
			return sReturn;
			//return "redirectform";
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("requestPost return null");
        return null;
        //return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
    
   
    public ResponseEntity<String> isToken()
    {
    	
    	// Start Session: POST /sm/startSession
    	String sessionId;
    	String msToken = null;
		try {
			sessionId = smConn.startSession();
			msToken = smConn.generateToken(sessionId, "RMms001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return new ResponseEntity<String>(msToken, HttpStatus.OK);
    }
    
    @Autowired
	HttpSession session;
    
    
//    @GetMapping("/urlFinishProcess")
//    public String acceptConsent (String sessionId,Model model, String requestSource,String pdsRequestSelection) throws Exception
//    {
//    	log.info("@@@@@@@@@@@@@@@@@@@@@@@@@2222ACCEPT: Entering urlFinishProcess ...");
//    	log.info("sessionID:"+sessionId);
//    	log.info("requestSource:"+requestSource);
//    	log.info("pdsSource:"+pdsRequestSelection);
//    	
//    	System.out.println("sesion"+session.getAttribute("sessionId"));
//    	sessionId = (String) session.getAttribute("sessionId");
//    	requestSource =(String) session.getAttribute("requestSource");
//    	pdsRequestSelection =(String) session.getAttribute("pdsSource");
//    	log.info("2.sessionID:"+sessionId);
//    	log.info("2.requestSource:"+requestSource);
//    	log.info("2.pdsSource:"+pdsRequestSelection);
//    	//return responseService.returnFromResponseUI (sessionId, model);
//    	//List<String> attrRequestList = new List<String>();
//    	return requestService.returnFromUI(sessionId, model, requestSource, pdsRequestSelection, null);
//    }
    
    
//    @GetMapping("redirection_form")
//    public String getHtmlRedirectForm(HttpSession session, Model model) throws Exception
//    {
//    	log.info("en getHtmlRedirectForm");
//    	return "redirectform";
//    }

}
