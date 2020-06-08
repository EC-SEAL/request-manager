package eu.atos.seal.rm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
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

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ResponseApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    //public ResponseEntity<Void> responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) 
    public String responsePost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken, Model model) 
    {
        log.info("responsePost called");
    	String accept = request.getHeader("Accept");
        
        try {
        	
			String sReturn = responseService.rmResponse(msToken, model);
			log.info("requestPost: sReturn="+sReturn);
			
			return sReturn;
			//return "redirectform";
		} 
        //catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
		//		| InvalidKeySpecException | IOException e) {
		catch (Exception e)
        {
			e.printStackTrace();
		}
        log.info("responsePost return null");
        return null;
    }

}
