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
import io.swagger.annotations.Api;

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
			errorMsg += "1 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        
	        return "rmError";
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
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        return "rmError";
			
		}
		dsResponse = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
		log.info("RequestAttributes: Reading dsResponse");
		//System.out.println("spRequest:"+spRequest.toString() );
					
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
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        return "rmError"; 
		}
		dsMetadata = (new ObjectMapper()).readValue(objDsMetadata.toString(),EntityMetadata.class);
		log.info("RequestAttributes: Reading dsMetadata");
		
		if (dsResponse.getStatus().getCode() == AttributeSetStatus.CodeEnum.ERROR)	// [TODO] Error especial revisar
		{	
			//TODO Que hacer si recibo una respuesta erronea
			log.error("dsResponse nos da un error");
			
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
		// Leo responseAssertions¿?
		
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
			log.error(errorMsg);
	        return "rmError";
		}
		
		
		//  Looking for the  endpoint to redirect
		// 
		String msName = getMsName(model, sessionId, null);
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
			String errorMsg= "responseAttributes: Exception calling SM (generateToken to"+msName+")  \n";
			errorMsg += "5 Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError";
		}
		String url = endPoint;
		model.addAttribute("msToken", tokenToSPms);
		model.addAttribute("UrlToRedirect", url);
		
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
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        
	        //return "acmError";
		}
		spMetadata = (new ObjectMapper()).readValue(objSpMetadata.toString(),EntityMetadata.class);
		log.info("readSpMetadata: Reading spMetadata");
		System.out.println("spMetadata:"+spMetadata.toString());
		return spMetadata;
	}
	
	public String getMsName(Model model, String sessionId,EntityMetadata spMetadata) throws IOException, JsonParseException, JsonMappingException
	//public String getMsName(Model model, String sessionId) throws IOException, JsonParseException, JsonMappingException
	{
		
		final String msName;
		
		
		//EntityMetadata spMetadata;
		if (spMetadata ==null)
		{
			spMetadata = readSpMetadata(model, sessionId);
		}
			
		if (spMetadata.getMicroservice() == null || spMetadata.getMicroservice().size()==0)
		{
			// ERROR
			String errorMsg= "Error getting microservice from spMetadata \n";
			
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError"; //[TODO]
		}
		msName = spMetadata.getMicroservice().get(0);
		log.info ("spMetadata msName:"+msName);
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
		{
			listPub= msopt.get().getPublishedAPI();
		}
		else
		{
			System.out.println("Error ms not found");
			return "error";//TODO
		}
		
		Optional<PublishedApiType> pubOpt = listPub.stream().filter(a->(a.getApiClass()==ApiClassEnum.SP && a.getApiCall().contains("handleRes"))).findAny();
		if (pubOpt.isPresent())
		{
			endPoint = pubOpt.get().getApiEndpoint();
			System.out.println("Endpoint:"+endPoint);
		}
		else
		{
			System.out.println("Error endpoint not found");
			return "error";//TODO
		}
		return endPoint;
	}

}
