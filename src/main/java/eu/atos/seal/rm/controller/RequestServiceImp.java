package eu.atos.seal.rm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.DataStore;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.service.cm.ConfMngrConnService;
import eu.atos.seal.rm.service.sm.SessionManagerConnService;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RequestServiceImp implements RequestService
{
	private static final Logger log = LoggerFactory.getLogger(RequestServiceImp.class);
	
	@Autowired
	private SessionManagerConnService smConnService;
	
	@Autowired
	private  ConfMngrConnService cmConnService;
	
	private String rmMsName="RMms001";

	@Override
	public String rmRequest(String token) throws JsonParseException, JsonMappingException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException
	{
		log.debug("rmResquest token recibido: "+token);
		// Revisar token recibido
		//				 
		if (token.endsWith("="))
		{
			token = token.replace("=", "");
		}
		if (token.startsWith("msToken="))
		{
			token = token.replace("msToken=", "");
		}
		
		// Antes de empezar compruebo que tengo los datos del CM rellenos si no es así lo relleno
		//fillCMData(); //[TODO]
		
		///
		///	VALIDATE TOKEN
		///
		String sessionId="";
		try
		{
			sessionId = smConnService.validateToken( token);
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (validateToken) with token:"+token+"\n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        
	        return "rmError";
		}
		
		//  
		//	READ VARIABLE "spRequest" 
		//  
		Object objSpRequest = null;
		AttributeSet spRequest = null;
		try
		{
			objSpRequest = smConnService.readVariable(sessionId, "spRequest");
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData spRequest)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        return "rmError";
			
		}
		spRequest = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
		log.info("RequestAttributes: Reading spRequest");
		//System.out.println("spRequest:"+spRequest.toString() );
				
		/// 
		///	READ VARIABLE		 "spMetadata" 
		///  		 
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
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        
	        //return "rmError"; //??
		}
		spMetadata = (new ObjectMapper()).readValue(objSpMetadata.toString(),EntityMetadata.class);
		log.info("RequestAttributes: Reading spMetadata");
		
		//
		// creamos el datastore
		// 
		
		///
		///  actualizamos datastore
		///
		
		///
		// creamos idpRequest
		///
		AttributeSet idpRequest = new AttributeSet();
		//[TODO] Rellenar idpRequest
		
		//		Completo la idpRequest	//[TODO]
		/*idpRequest.setId( UUID.randomUUID().toString());
		idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
		idpRequest.setIssuer( spRequest.getIssuer());
		idpRequest.setRecipient( idpMetadata.getEntityId());
		idpRequest.setProperties( spRequest.getProperties());
		idpRequest.setLoa( spRequest.getLoa());*/
		
		///
		// actualizamos idpRequest en SM
		///
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"idpRequest",objMapper.writeValueAsString(idpRequest));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable idpRequest)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "acmError";
		}
			
		
		/// 
		/// creamos idpMetadata
		///
		EntityMetadata idpMetadata = null;
		//idpMetadata = entityListIDP.get(0); //[TODO]
		
		///
		/// actualizamos idpMetadata en SM
 		///
		objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"idpMetadata",objMapper.writeValueAsString(idpMetadata));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable idpMetadata)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError";
		}
		
		///
		///	Creamos dataStore
		///
		DataStore dataStore = new DataStore();
		//[TODO]Rellenar/leer/modificar ...
		
		///
		///	Actualizamos dataStore
		///
		objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"dataStore",objMapper.writeValueAsString(dataStore));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable dataStore)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError";
		}
		
		
		
		///
		///	Creamos ClientCallbackAddr
		///
		
		
		///
		///	Actualizamos ClientCallbackAddr
		///
		
		
		
		
		///
		/// GenerateToken for IDP uc 8.01
		///
		String msName="";
		msName = idpMetadata.getMicroservice().get(0);
		String tokenToIDPms = smConnService.generateToken(sessionId,msName);
		System.out.println("tokenToIDPms:"+tokenToIDPms);
		
		
		
		
		return null;
	}

}
