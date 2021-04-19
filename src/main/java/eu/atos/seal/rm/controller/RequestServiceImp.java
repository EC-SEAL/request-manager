package eu.atos.seal.rm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.ApiClassEnum;
import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.AttributeType;
import eu.atos.seal.rm.model.AttributeTypeList;
import eu.atos.seal.rm.model.DataStore;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.model.MsMetadata;
import eu.atos.seal.rm.model.MsMetadataList;
import eu.atos.seal.rm.model.PublishedApiType;
import eu.atos.seal.rm.service.cm.ApigwclConnServiceImp;
import eu.atos.seal.rm.service.cm.ConfMngrConnService;
import eu.atos.seal.rm.service.sm.SessionManagerConnService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.AttributeList;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
	
	@Autowired
	private ApigwclConnServiceImp apigwclConnService;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private  ResponseServiceImp respService;
	
	private String rmMsName="RMms001";
	
	MsMetadataList msmtdlist = null;

	private Model model=null;

	
	
    @PostMapping("request_client")
    public String getRequest(@RequestBody MultiValueMap<String, String> formData,
            HttpSession session, Model model)
    {
    	
    	System.out.println("Received request from UI" + formData);
    	
        String[] attrRequestList = formData.get("attrRequestList").get(0).split(",");
        
        AttributeTypeList attributesRequestList = (AttributeTypeList) session
                .getAttribute("attributesRequestList");

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
        
        session.setAttribute("attributesRequestList", attributesRequestListNew);        
        session.setAttribute("sessionId", session.getAttribute("sessionId"));
        return "redirect:" + urlReturn;
        
        
    }
    
    
	@Override
	public String rmRequest(String token, Model model) throws JsonParseException, JsonMappingException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException
	{
		log.debug("rmResquest token recibido: "+token);
		System.out.println("rmResquest token recibido: "+token);
		this.model = model;
		
//		EntityMetadataList listIDP = cmConnService.getEntityMetadataSet("IdP");
//		if ( listIDP !=null)
//		{
//				System.out.println("getEntityMetadataSet(idp)"+listIDP.toString());
//		}
//		System.out.println("PRUEBA-A: "+cmConnService.getExternalEntities());
//		//System.out.println("PRUEBA-B: "+cmConnService.getEntityMetadataSet("eIDAS"));
//		System.out.println("PRUEBA-B: "+cmConnService.getEntityMetadataSet("EIDAS"));
//		if (cmConnService.getEntityMetadata("AUTHSOURCE","eIDAS")!=null)
//		{
//			System.out.println("EntityMetadata(EIDAS)"+cmConnService.getEntityMetadata("AUTHSOURCE","eIDAS").toString());
//		}
//		if(cmConnService.getEntityMetadata("AUTHSOURCE","eduGAIN") !=null)
//		{
//		
//			System.out.println("EntityMetadata(EDUGAIN)"+cmConnService.getEntityMetadata("AUTHSOURCE","eduGAIN").toString());
//		}
////		if (apigwclConnService.getCollectionList("PERSISTENCE")!=null)
////		{
////			System.out.println("PDS:  "+apigwclConnService.getCollectionList("PERSISTENCE").toString());
////		}
////		else
////		{System.out.println("PDS:null");}
////		if (apigwclConnService.getCollectionList("SSI")!=null)
////		{
////			System.out.println("SSI:  "+apigwclConnService.getCollectionList("SSI").toString());
////		}
////		{System.out.println("SSI:null");}
		
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
		
		if (cmConnService != null)
		{
			String thisCL = "";
			msmtdlist = cmConnService.getAllMicroservices();
			
			if (msmtdlist ==null)
			{
				System.out.println("msmtdlist null");
			log.debug("msmtdlist null");
			}
			else
			{System.out.println("msmtdlist size"+msmtdlist.size());
		
				log.debug("msmtdlist size"+msmtdlist.size());
			}
					//getMicroservicesByApiClass("CL")
					//.get(0).getMsId();
			//log.debug("thisCL: "+thisCL);
		}
		else
		{
			log.debug("cmConnService null");
		}
		// Antes de empezar compruebo que tengo los datos del CM rellenos si no es así lo relleno
		//fillCMData(); //[TODO]
		
		///
		///	VALIDATE TOKEN
		///
		String sessionId = validateToken(token);
		
		//  
		//	READ VARIABLE "spRequest" 
		//  
		log.info("RequestAttributes: Reading spRequest");
		AttributeSet spRequest = readSpRequest(sessionId);
		//System.out.println("spRequest:"+spRequest.toString() );
				
		/// 
		///	READ VARIABLE		 "spMetadata" 
		/// 
		log.info("RequestAttributes: Reading spMetadata");
		EntityMetadata spMetadata = readSpMetadata(sessionId);
		
//		////
//		////  QUITAR (es una prueba del response
//		///
//		respService = new ResponseServiceImp();
//		String msName= respService.getMsName(model, sessionId,spMetadata);
//		String endPoint= respService.getSpResponseEndpoint(model, msName,cmConnService);
//		System.out.println("ENDPOINT RESPONSE: "+endPoint);
//		////
//		////
//		///
		
		
		//  
		//	READ VARIABLE "spRequestEP" 
		//  
		log.info("RequestAttributes: Reading spRequestEP");
		String spRequestEP = readSpRequestEP(sessionId);

		//  
		//	READ VARIABLE "spRequestSource" 
		//  
		log.info("RequestAttributes: Reading spRequestSource");
		String spRequestSource = readSpRequestSource(sessionId);
		
		
		/// Pongo en SM mi direccion de retorno
		String callbackURL=null;
		if (msmtdlist!=null)
		{
			//msmtdlist.getApicallMs("RM").get;
			 List<PublishedApiType> listAPIs=  msmtdlist.getMs("RMms001").getPublishedAPI();
			 for (int i=0;i< listAPIs.size();i++)
			 {
				 if (listAPIs.get(i).getApiCall().contains("rmResponse"))
				 {
					 callbackURL=listAPIs.get(i).getApiEndpoint();
					 log.info("ClientCallbackAddr:"+callbackURL);
				 }
			 }
			 
			 if(callbackURL!=null)
			 {
				 try
				 {
					 smConnService.updateVariable(sessionId,"ClientCallbackAddr",callbackURL);
				 }
				 catch (Exception ex)
				 {
					 String errorMsg= "Exception calling SM (updateVariable ClientCallbackAddr)  \n";
					 errorMsg += "Exception message:"+ex.getMessage()+"\n";
					//model.addAttribute("ErrorMessage",errorMsg);
					log.error(errorMsg);
				 }
				 
			 }
		}
		
		//Por defecto le doy un valor a la variable isDiscovery
		smConnService.updateVariable(sessionId,"isDiscovery","FALSE");
	
		
		//[DELETE
		if (spRequestEP==null)
		{
			spRequestEP="auth_request";
		}
		if (spRequestSource==null || spRequestSource.length() == 0) // 20201001 Paco on skype
		{
			//spRequestSource="eIDAS";
			spRequestSource="Discovery";
		}
		String collectionId ="";
		if (spRequestEP.contains("auth"))
		{
			collectionId = "AUTHSOURCE";
		}
		else
		{
			collectionId = "DATAQUERYSOURCES";
		}
		EntityMetadataList sourceList = cmConnService.getEntityMetadataSet(collectionId);
		
		/// 
		/// Select where to go
		/// 
		if (spRequestEP.contains("auth"))
		{
			if (spRequestSource.contains("Discovery"))
			{
				
				return goToSelectIUI( model, sessionId, spRequest, sourceList);
			}
			else  //Should be eIDAS of eduGAIN
			{
				return prepareAndGoToIdp(sessionId,spRequest,spMetadata,spRequestSource,null);
			}
		}
		else //data_query or null ¿puede ser null?
		{
			if (spRequestSource.contains("Discovery"))
			{
				
				return goToSelectIUI( model, sessionId, spRequest, sourceList);
			}
			else
			{
				return prepareAndGoToAP(sessionId, spRequest,spMetadata,spRequestSource);
			}
		}
	}
//		
//		
//		//
//		// creamos el datastore
//		// 
//		
//		///
//		///  actualizamos datastore
//		///
//		
//		///
//		// creamos idpRequest
//		///
//		AttributeSet idpRequest = new AttributeSet();
//		//[TODO] Rellenar idpRequest
//		
//		//		Completo la idpRequest	//[TODO]
//		/*idpRequest.setId( UUID.randomUUID().toString());
//		idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
//		idpRequest.setIssuer( spRequest.getIssuer());
//		idpRequest.setRecipient( idpMetadata.getEntityId());
//		idpRequest.setProperties( spRequest.getProperties());
//		idpRequest.setLoa( spRequest.getLoa());*/
//		
//		///
//		// actualizamos idpRequest en SM
//		///
//		ObjectMapper objMapper = new ObjectMapper();
//		try
//		{
//			smConnService.updateVariable(sessionId,"idpRequest",objMapper.writeValueAsString(idpRequest));
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (updateVariable idpRequest)  \n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//			log.error(errorMsg);
//	        return "acmError";
//		}
//			
//		
//		/// 
//		/// creamos idpMetadata
//		///
//		EntityMetadata idpMetadata = null;
//		//idpMetadata = entityListIDP.get(0); //[TODO]
//		
//		///
//		/// actualizamos idpMetadata en SM
// 		///
//		objMapper = new ObjectMapper();
//		try
//		{
//			smConnService.updateVariable(sessionId,"idpMetadata",objMapper.writeValueAsString(idpMetadata));
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (updateVariable idpMetadata)  \n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//			log.error(errorMsg);
//	        return "rmError";
//		}
//		
//		///
//		///	Creamos dataStore
//		///
//		DataStore dataStore = new DataStore();
//		//[TODO]Rellenar/leer/modificar ...
//		
//		///
//		///	Actualizamos dataStore
//		///
//		objMapper = new ObjectMapper();
//		try
//		{
//			smConnService.updateVariable(sessionId,"dataStore",objMapper.writeValueAsString(dataStore));
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (updateVariable dataStore)  \n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//			log.error(errorMsg);
//	        return "rmError";
//		}
//		
//		
//		
//		///
//		///	Creamos ClientCallbackAddr
//		///
//		
//		
//		///
//		///	Actualizamos ClientCallbackAddr
//		///
//		
//		
//		
//		
//		///
//		/// GenerateToken for IDP uc 8.01
//		///
//		String msName="";
//		msName = idpMetadata.getMicroservice().get(0);
//		String tokenToIDPms = smConnService.generateToken(sessionId,msName);
//		System.out.println("tokenToIDPms:"+tokenToIDPms);
		
		
		

	


	private String prepareAndGoToIdp(String sessionId, AttributeSet spRequest, EntityMetadata spMetadata, String spRequestSource, List<AttributeType> newAttributeList) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
//		System.out.println("getEntityMetadataSet(idp)"+cmConnService.getEntityMetadataSet("IdP").toString());
//		System.out.println("EntityMetadata(EIDAS)"+cmConnService.getEntityMetadata("AUTHSOURCE","EIDAS").toString());
//		System.out.println("EntityMetadata(EDUGAIN)"+cmConnService.getEntityMetadata("AUTHSOURCE","EDUGAIN").toString());
//		
		log.info("En prepareAndGoToIdp");
		
		
		EntityMetadata authMetadata0 = cmConnService.getEntityMetadata("AUTHSOURCE", spRequestSource); // Reading the AUTHSOURCEmetadata.json

		String msName = authMetadata0.getMicroservice().get(0);
		String endpoint= getEndpoint("auth",msName);
		
		System.out.println("[prepareAndGoToId] authMetadata:"+authMetadata0.toString());
		System.out.println("€€€€€€¬¬¬¬¬¬¬ endpoint"+endpoint+" msName:"+msName);
		
		
//		if (authMetadata0!=null)
//		{
//			System.out.println("[prepareAndGoToId] authMetadata:"+authMetadata0.toString());
//			msName= authMetadata0.getMicroservice().get(0);
//			System.out.println("msName:"+msName);
//			MsMetadata msMetadata = msmtdlist.getMs(msName);
//			System.out.println("[prepareAndGoToId] msMetadata:"+msMetadata.toString());
//			List<PublishedApiType> list = msMetadata.getPublishedAPI();
//			for (PublishedApiType publishedApiType : list) {
//				if (publishedApiType.getApiCall().contains("auth"))
//				{
//					endpoint = publishedApiType.getApiEndpoint();
//					break;
//				}
//			}
//		}
		System.out.println("[prepareAndGoToId] endpoint:"+endpoint);
		//spRequestSource= {Discovery, PDS, uportSSIwallet, eIDAS, eduGAIN}
		/*
		if (spRequestSource.contains("eIDAS"))
		{
			//MsMetadataList authMS = msmtdlist.getApicallMs("authenticate");
			//authMS.ge
			
			 
			//"eIDASms_001"
			//msmtdlist.getApicallMs(authMetadata0.getMicroservice().get(0))
		}
		else if (spRequestSource.contains("eduGAIN"))
		{}
		else
		{
			//ERROR
			log.error("prepareAndGoToIdp: spRequestSource( "+spRequestSource+")value error");
			System.out.println("prepareAndGoToIdp: spRequestSource( "+spRequestSource+")value error");
			return null;
		}
		*/
		
		
		/// 
		/// creamos idpMetadata
		///
		EntityMetadata idpMetadata = null;
		EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet(spRequestSource.toUpperCase()); 
		if ((eMTDList!=null)&&(eMTDList.size()>0))
		{
			idpMetadata = eMTDList.get(0);
		}
		///
		// creamos idpRequest
		///
		AttributeSet idpRequest = new AttributeSet();
		idpRequest.setId( UUID.randomUUID().toString());
		idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
		idpRequest.setIssuer( spRequest.getIssuer());
		idpRequest.setProperties( spRequest.getProperties());
		if (idpMetadata!=null)
		{
			idpRequest.setRecipient( idpMetadata.getEntityId());
		}
		idpRequest.setLoa( spRequest.getLoa());
		idpRequest.setAttributes(spRequest.getAttributes());
		
		
		///
		/// actualizamos idpMetadata en SM
 		///
		ObjectMapper objMapper = new ObjectMapper();
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
		// actualizamos idpRequest en SM
		///
		objMapper = new ObjectMapper();
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

		String token = smConnService.generateToken(sessionId, msName);
		System.out.println("prepareAndGoToIDP:Create token to "+msName+" tokenValue:"+token);
		//System.out.println("redirect to: "+endpoint);
		
		this.model.addAttribute("msToken", token);
		this.model.addAttribute("UrlToRedirect", endpoint);
		log.info("En prepareAndGoToIDP spRequestSource: "+spRequestSource);
		log.info("urlToRedirect 	"+endpoint);
	
		return "redirectform";
		//return "redirect:/redirect.html";
	}

	private String prepareAndGoToAP(String sessionId, AttributeSet spRequest, EntityMetadata spMetadata, String spRequestSource) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException 
	{
		// TODO Auto-generated method stub
		log.info("En prepareAndGoToAP");
		System.out.println("[prepareAndGoToAP] ");
		String endpoint="";
		String msName = "";
		String apiCall ="";

		EntityMetadataList emList = cmConnService.getEntityMetadataSet("DATAQUERYSOURCES");
		if (emList==null || emList.size()<=0)
		{
			System.out.println("[prepareAndGoToAP] emList NULLLLLLLLL");
			return "error";
		}
		else
		{
			System.out.println("[prepareAndGoToAP] emList:"+emList.toString());
		}
		
		if (spRequestSource.equalsIgnoreCase("PDS"))
		{
			spRequestSource="PERSISTENCE";
			apiCall = "load";
			
			//Null ,Mobile, Browser, googleDrive, oneDrive
		}
		else if (spRequestSource.equalsIgnoreCase("eIDAS")||spRequestSource.equalsIgnoreCase("eduGAIN"))
		{
			apiCall="query";
			//apiCall ="authenticate";
		}
		else
		{
			apiCall = "issue";
		}
		//EntityMetadata dataMetadata = cmConnService.getEntityMetadata("DATAQUERYSOURCES", spRequestSource);
		EntityMetadataList dataMetadatas = cmConnService.getEntityMetadataSet(spRequestSource);
		if (dataMetadatas==null)
		{
			System.out.println("[prepareAndGoToAP] dataMetadatas NULLLLLLLLL");
		}
		else
		{
			System.out.println("[prepareAndGoToAP] dataMetadatas("+spRequestSource+"):"+dataMetadatas.toString());
		}
		
		//SELECT between the different dataMetadatas
		int size = dataMetadatas.size();
		int selected = 0; //REVIEW
		
		EntityMetadata dqMetadata= dataMetadatas.get(selected);
		msName = dqMetadata.getMicroservice().get(0);
		endpoint = getEndpoint(apiCall, msName);
		
		String token = smConnService.generateToken(sessionId, msName);
		System.out.println("Create token to "+msName+" tokenValue:"+token);
		System.out.println("redirect to: "+endpoint);
		
		this.model.addAttribute("msToken", token);
		this.model.addAttribute("UrlToRedirect", endpoint);
		
		if (spRequestSource.equalsIgnoreCase("PERSISTENCE"))
		{
			return "redirectform2"; // redirectform2 es GET
		}
		else
		{
			return "redirectform";
		}
	}
	
	
	
		
		//	{
//		// TODO Auto-generated method stub
//		log.info("en goToSelectApUI");
//		//Rellenar EntityMetadataList
//		// y ponerla en la variable sourceList
//		//EntityMetadataList sourceList = new EntityMetadataList();
//		session.setAttribute("sourceList",sourceList);
//		
//		//Rellenar atributos con la spRequest
//		// y ponerla en la vble de sesion: attributeRequestList
//		AttributeTypeList attributeRequestList = new AttributeTypeList();
//
//		attributeRequestList.addAll(spRequest.getAttributes());
//		session.setAttribute("attributesRequestList", attributeRequestList);
//		System.out.println("This is  name: " + attributeRequestList.get(0).getName()	);
//		System.out.println("This is friendly name: " + attributeRequestList.get(0).getFriendlyName());
//
//		//Rellenar spMetadata
//		//con la vble spMetadata
//		session.setAttribute("spMetadata", spMetadata);
//		
//		return "redirect:../request_client";  //REVIEW
//	}

	@Value("${rm.multiui.privacyPolicy}") //Defined in application.properties file
    String privacyPolicy;
	private String goToSelectIUI(Model model, String sessionId, AttributeSet spRequest, EntityMetadataList sourceList)
	{
		// TODO Auto-generated method stub
		log.info("en goToSelectUI");
		
		AttributeTypeList attributeRequestList = new AttributeTypeList();

		attributeRequestList.addAll(spRequest.getAttributes());
		session.setAttribute("attributesRequestList", attributeRequestList);
		session.setAttribute("sourceList",sourceList);
		session.setAttribute("urlReturn", "request_client/return");
		session.setAttribute("sessionId", sessionId);
		if (privacyPolicy != null)
			session.setAttribute("privacyPolicy",privacyPolicy);
		
		return "redirect:../request_client";
	}	
	
	//Auxiliary methods
	private String validateToken(String token)
	{
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
		return sessionId;
	}
	
	private AttributeSet readSpRequest(String sessionId) 
			throws IOException, JsonParseException, JsonMappingException 
	{
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
	        //return "rmError";
	        return null;
		}
		if (objSpRequest!=null) //==null no existe la vble en SM
		{
			spRequest = (new ObjectMapper()).readValue(objSpRequest.toString(),AttributeSet.class);
			log.info("RequestAttributes: Reading spRequest");
		}
		return spRequest;
	}
	
	private EntityMetadata readSpMetadata(String sessionId)
			throws IOException, JsonParseException, JsonMappingException 
	{
		
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
		if (objSpMetadata!=null)
		{
			spMetadata = (new ObjectMapper()).readValue(objSpMetadata.toString(),EntityMetadata.class);
			log.info("RequestAttributes: Reading spMetadata");
		}
		return spMetadata;
	}
	
	private String readSpRequestSource(String sessionId) 
	{
		String spRequestSource="";
		try
		{
			spRequestSource = (String)smConnService.readVariable(sessionId, "spRequestSource");
			log.info("spRequestSource readed:"+spRequestSource);
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData spRequestSource)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
		}
		return spRequestSource;
	}
	
	private String readSpRequestEP(String sessionId) 
	{
		String spRequestEP="";
		try
		{
			spRequestEP = (String)smConnService.readVariable(sessionId, "spRequestEP");
			log.info("spRequestEP readed:"+spRequestEP);
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (getSessionData spRequestEP)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        System.out.println("Devuelvo error "+errorMsg);
	        
			
		}
		return spRequestEP;
	}
	
	//https://vm.project-seal.eu:9053/cl/list/Eidas

	public String getEndpoint(  String apiCall, String msName)
	{	
		String endpoint ="";
		//String msName ="";
		
		
		
		System.out.println("msName:"+msName);
		MsMetadata msMetadata = msmtdlist.getMs(msName);
		System.out.println("[prepareAndGoToId] msMetadata:"+msMetadata.toString());
		List<PublishedApiType> list = msMetadata.getPublishedAPI();
		for (PublishedApiType publishedApiType : list) {
			if (publishedApiType.getApiCall().contains(apiCall))
			{
				endpoint = publishedApiType.getApiEndpoint();
				break;
			}
		}
		
		return endpoint;
	}
		
	
	// ¿SE SUPONE QUE SE LLEGA DESDE REJECT PERO DE MOMENTO NO FUNCIONA
	@Override
	public String returnNothing(String sessionId, Model model)
	{
		//sessionId= "f66d6165-aa4d-4e6a-898e-46faea16d6cf"; //TODO Quitar

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
	
	@Override
	public String returnFromRequestUI(String sessionId, Model model)
	{
		log.info("En returnFromRequestUI");
		log.info("attrRequestList:"+ session.getAttribute("attrRequestList"));
		log.info("requestSource:"+session.getAttribute("requestSource"));
		log.info("pdsRequestSelection"+session.getAttribute("pdsRequestSelection"));
		 //session.setAttribute("pdsRequestSelection", pdsRequestSelection);
		log.info("sessionId"+sessionId);
		log.info("sessionId"+session.getAttribute("sessionId"));
		
//		List<String> attrRequestListSelected = (List<String>)session.getAttribute("attrRequestList");
//		log.info("requestList: "+attrRequestListSelected.toString());
		sessionId = (String) session.getAttribute("sessionId");
		
		
		
		AttributeSet spRequest;
		EntityMetadata spMetadata;
		try 
		{
			spRequest = readSpRequest(sessionId);
			spMetadata = readSpMetadata(sessionId);
		} 
		catch (IOException e)
		{
			// TODO Error control
			e.printStackTrace();
			return "error";
		}
		//List<AttributeType> attReqList = spRequest.getAttributes();
		String[] attrRequestSelectedArray = (String[])session.getAttribute("attrRequestList");
		List<String> attrRequestSelectedList =Arrays.asList( attrRequestSelectedArray );
		List<AttributeType> newAttributeList = new AttributeTypeList();
		for( AttributeType attribute:spRequest.getAttributes())
		{
			System.out.println("Friendly:"+attribute.getFriendlyName()+ " name:"+attribute.getName());
			if (attrRequestSelectedList.contains(attribute.getFriendlyName()) || attrRequestSelectedList.contains(attribute.getName()))
			{
				newAttributeList.add(attribute);
			}
		}
		AttributeSet newSpRequest = new AttributeSet();
		newSpRequest.setId( UUID.randomUUID().toString());
		newSpRequest.setType(AttributeSet.TypeEnum.REQUEST);
		newSpRequest.setIssuer( spRequest.getIssuer());
		newSpRequest.setProperties( spRequest.getProperties());
		
		newSpRequest.setLoa( spRequest.getLoa());
		//newSpRequest.setAttributes(spRequest.getAttributes());
		newSpRequest.setAttributes(newAttributeList);
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"spRequestModified",objMapper.writeValueAsString(newSpRequest));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable spRequestModified)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			log.error(errorMsg);
		}
		
		String requestSource = (String) session.getAttribute("requestSource");
		String spRequestSource ="";//Discovery, PDS, SSI, eIDAS, eduGAIN		
		if (requestSource.contains("eidas"))
		{ 
			spRequestSource = "eIDAS";
		}
		else if (requestSource.contains("edugain"))
		{
			spRequestSource = "eduGAIN";
		}
		else if (requestSource.contains("ssi"))
		{
			spRequestSource = "SSI";
		}
		else if (requestSource.contains("pds"))
		{
			spRequestSource = "PDS";
			String pdsRequestSelection = session.getAttribute("pdsRequestSelection").toString();
			try
			{
			smConnService.updateVariable(sessionId,"PDS",pdsRequestSelection);
			}
			catch (Exception ex)
			{
				 String errorMsg= "Exception calling SM (updateVariable PDS)  \n";
				 errorMsg += "Exception message:"+ex.getMessage()+"\n";
				//model.addAttribute("ErrorMessage",errorMsg);
				log.error(errorMsg);
			}
		}

		
		
		// Cambiar la variable spRequestSource en el SM de Discovery a su actual valor
		try
		{
			smConnService.updateVariable(sessionId,"spRequestSource",spRequestSource);
			smConnService.updateVariable(sessionId,"isDiscovery","TRUE");
		}
		catch (Exception ex)
		{
			 String errorMsg= "Exception calling SM (updateVariable spRequestSource)  \n";
			 errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
		}
		
		if (spRequestSource.equalsIgnoreCase("eIDAS") || spRequestSource.equalsIgnoreCase("edugain"))
		{
			try
			{
				log.info("llamo a prepareAndGoToIdp");
				
				//return redirectToIDP(sessionId, model, spRequest, spRequestSource);
				return redirectToIDP(sessionId, model, newSpRequest, spRequestSource);
				
				//return prepareAndGoToIdp( sessionId, spRequest, spMetadata, spRequestSource, newAttributeList);
				//
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return "error";
			}
		}
		else
		{
			try
			{
				//return redirectToAP(sessionId, model, spRequest, spRequestSource);
				if (spRequestSource.equalsIgnoreCase("PDS"))
				{
					log.info("llamo a redirectToAP(PDS)");
					return redirectToAP(sessionId, model, newSpRequest, spRequestSource);
				}
				else
				{
					log.info("llamo a redirectToSSI");
					return redirectToSSI(sessionId, model, newSpRequest, spRequestSource);
				}
			} 
			catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
					| InvalidKeySpecException | IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
		}
				
//		sessionId= "f66d6165-aa4d-4e6a-898e-46faea16d6cf"; //TODO Quitar
//
//		ObjectMapper objMapper = new ObjectMapper();
//		String endPoint = null;
//		try
//		{
//			// Updating the responseAssertions consented by the user: none
//			smConnService.updateVariable(sessionId,"responseAssertions",objMapper.writeValueAsString(null));
//		
//			String msName = getMsName(model, sessionId, null); // Returning the FIRST ONE! ***
//			endPoint = getSpResponseEndpoint(model, msName,cmConnService);
//			log.info ("UrlToRedirect: " + endPoint);
//			if (endPoint == null  || endPoint.contains("error"))
//			{
//				model.addAttribute("ErrorMessage","SP endpoint not found");
//				return "fatalError";
//			}
//				
//			String tokenToSPms = "";
//			tokenToSPms = smConnService.generateToken(sessionId,msName); 
//		
//			model.addAttribute("msToken", tokenToSPms);
//			model.addAttribute("UrlToRedirect", endPoint);
//			log.info("En returnFromRequestUI UrlToRedirect"+endPoint);
//			return "redirectform";
//		
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= ex.getMessage()+"\n";
//			log.info ("Returning error: "+errorMsg);
//			
//			model.addAttribute("ErrorMessage",errorMsg);
//			if (endPoint != null) 
//	        	return "rmError"; 
//	        else
//	        	return "fatalError"; // Unknown endPoint...
//		}
	}


	private String redirectToIDP(String sessionId, Model model, AttributeSet spRequest, String spRequestSource)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException 
	{
		
		EntityMetadata authMetadata0;
		String msName;
		String endpoint;
		
		if (spRequestSource.equalsIgnoreCase("eidas")||spRequestSource.equalsIgnoreCase("edugain"))
		{
			authMetadata0 = cmConnService.getEntityMetadata("AUTHSOURCE", spRequestSource); // Reading the AUTHSOURCEmetadata.json
			msName = authMetadata0.getMicroservice().get(0);
			endpoint= getEndpoint("auth",msName);
		}
		else
		{
			String apiCall;
			if (spRequestSource.contains("PDS"))
			{
				spRequestSource="PERSISTENCE";
				apiCall = "load";
				
				//Null ,Mobile, Browser, googleDrive, oneDrive
			}
			else
			{
				apiCall = "issue";
			}
			//EntityMetadata dataMetadata = cmConnService.getEntityMetadata("DATAQUERYSOURCES", spRequestSource);
			EntityMetadataList dataMetadatas = cmConnService.getEntityMetadataSet(spRequestSource);
		
			int size = dataMetadatas.size();
			int selected = 0; //REVIEW
			
			EntityMetadata dqMetadata= dataMetadatas.get(selected);
			msName = dqMetadata.getMicroservice().get(0);
			endpoint = getEndpoint(apiCall, msName);
		}
		
		////
		////
		////
		/// 
		/// creamos idpMetadata
		///
		EntityMetadata idpMetadata = null;
		//EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet("EIDAS");
		if (spRequestSource.equalsIgnoreCase("PDS"))
		{
			spRequestSource= "PERSISTENCE";
		}
		EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet(spRequestSource.toUpperCase());
		if ((eMTDList!=null)&&(eMTDList.size()>0))
		{
			idpMetadata = eMTDList.get(0);
		}
		///
		// creamos idpRequest
		///
		AttributeSet idpRequest = new AttributeSet();
		idpRequest.setId( UUID.randomUUID().toString());
		idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
		idpRequest.setIssuer( spRequest.getIssuer());
		idpRequest.setProperties( spRequest.getProperties());
		if (idpMetadata!=null)
		{
			idpRequest.setRecipient( idpMetadata.getEntityId());
		}
		idpRequest.setLoa( spRequest.getLoa());
		idpRequest.setAttributes(spRequest.getAttributes());  //TODO: spRequest
		
		
		///
		/// actualizamos idpMetadata en SM
		///
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ idpMetadata:"+idpMetadata.toString());
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
		// actualizamos idpRequest en SM
		///
		objMapper = new ObjectMapper();
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
		    return "fatalError";
		}
		
		
		
		
		
		
		////
		////
		////
		
		String tokenToSPms = "";
		tokenToSPms = smConnService.generateToken(sessionId,msName); 
		System.out.println("redirectToIDP: token to "+msName+" tokenValue:"+tokenToSPms);
		model.addAttribute("msToken", tokenToSPms);
		model.addAttribute("UrlToRedirect", endpoint);
		log.info("En redirectToIDP spRequestSource: "+spRequestSource);
		log.info("urlToRedirect 	"+endpoint);
		return "redirectform";
	}
	
	
	
	
	private String redirectToAP(String sessionId, Model model, AttributeSet spRequest, String spRequestSource)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException 
	{
		
		/* TESTING
		sessionId = "51c6f957-8541-4699-a5c3-5ac0539e75ae"; // with session data
		smConnService.updateVariable(sessionId, "ClientCallbackAddr", "https://vm.project-seal.eu:9063/rm/response");
		smConnService.updateVariable(sessionId, "spRequestEP", "data_query");
		
		//String mySpRequest = "{\"id\":\"_d645d111cf100dfa46ace16ed3b208f0f2e867db83\",\"type\":\"Request\",\"issuer\":\"https:\\/\\/clave.sir2.rediris.es\\/module.php\\/saml\\/sp\\/saml2-acs.php\\/q2891006e_ea0002678\",\"recipient\":null,\"inResponseTo\":null,\"loa\":\"http:\\/\\/eidas.europa.eu\\/LoA\\/low\",\"notBefore\":\"2019-03-05T15:11:41Z\",\"notAfter\":\"2019-03-05T15:16:41Z\",\"status\":{\"code\":null,\"subcode\":null,\"message\":null},\"attributes\":[{\"name\":\"http:\\/\\/eidas.europa.eu\\/attributes\\/naturalperson\\/PersonIdentifier\",\"friendlyName\":\"PersonIdentifier\",\"encoding\":null,\"language\":null,\"mandatory\":true,\"values\":null},{\"name\":\"http:\\/\\/eidas.europa.eu\\/attributes\\/naturalperson\\/CurrentGivenName\",\"friendlyName\":\"FirstName\",\"encoding\":null,\"language\":null,\"isMandatory\":true,\"values\":null},{\"name\":\"http:\\/\\/eidas.europa.eu\\/attributes\\/naturalperson\\/CurrentFamilyName\",\"friendlyName\":\"FamilyName\",\"encoding\":null,\"language\":null,\"isMandatory\":true,\"values\":null}],\"properties\":{\"SAML_RelayState\":\"\",\"SAML_RemoteSP_RequestId\":\"_193600a923e1959d375e21fb3d216879\",\"SAML_ForceAuthn\":true,\"SAML_isPassive\":false,\"SAML_NameIDFormat\":\"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent\",\"SAML_AllowCreate\":\"true\",\"SAML_ConsumerURL\":\"http:\\/\\/lab9054.inv.uji.es\\/~paco\\/clave\\/secure.php?aaaa=1&bbbb=2\",\"SAML_Binding\":\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\",\"EIDAS_ProviderName\":\"ojetecalor_uojetecalor\",\"EIDAS_IdFormat\":\"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent\",\"EIDAS_SPType\":\"public\",\"EIDAS_Comparison\":\"minimum\",\"EIDAS_LoA\":\"http:\\/\\/eidas.europa.eu\\/LoA\\/low\",\"EIDAS_country\":null}}";       
		ObjectMapper objMapper = new ObjectMapper();		
		smConnService.updateVariable(sessionId, "spRequest", objMapper.writeValueAsString(spRequest));
		END TESTING*/
		
		
		EntityMetadata authMetadata0;
		String msName="";
		String endpoint;
		
//		if (spRequestSource.equalsIgnoreCase("eidas")||spRequestSource.equalsIgnoreCase("edugain"))
//		{
//			authMetadata0 = cmConnService.getEntityMetadata("AUTHSOURCE", spRequestSource); // Reading the AUTHSOURCEmetadata.json
//			msName = authMetadata0.getMicroservice().get(0);
//			endpoint= getEndpoint("auth",msName);
//		}
//		else
//		{
			String apiCall;
			if (spRequestSource.contains("PDS"))
			{
				spRequestSource="PERSISTENCE";
				apiCall = "load";
				
				//Null ,Mobile, Browser, googleDrive, oneDrive
			}
			else
			{
				apiCall = "query";
			}
			//EntityMetadata dataMetadata = cmConnService.getEntityMetadata("DATAQUERYSOURCES", spRequestSource);
			EntityMetadataList dataMetadatas = cmConnService.getEntityMetadataSet(spRequestSource);
		
			int size = dataMetadatas.size();
			int selected = 0; //REVIEW
			
			if (spRequestSource.contains("PERSISTENCE"))
			{
				EntityMetadata dqMetadata= dataMetadatas.get(selected);
				msName = dqMetadata.getMicroservice().get(0);
				System.out.println("msName obtenido: "+msName);
				msName="PERms001";
			}
			else if (spRequestSource.contains("SSI"))
			{
				msName = "SSI-IdP";
			}
			
			endpoint = getEndpoint(apiCall, msName);
//		}
		
		////
		////
		////
		/// 
		/// creamos idpMetadata
		///
//		EntityMetadata idpMetadata = null;
//		//EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet("EIDAS");
//		if (spRequestSource.equalsIgnoreCase("PDS"))
//		{
//			spRequestSource= "PERSISTENCE";
//		}
//		EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet(spRequestSource.toUpperCase());
//		if ((eMTDList!=null)&&(eMTDList.size()>0))
//		{
//			idpMetadata = eMTDList.get(0);
//		}
//		///
//		// creamos idpRequest
//		///
//		AttributeSet idpRequest = new AttributeSet();
//		idpRequest.setId( UUID.randomUUID().toString());
//		idpRequest.setType(AttributeSet.TypeEnum.REQUEST);
//		idpRequest.setIssuer( spRequest.getIssuer());
//		idpRequest.setProperties( spRequest.getProperties());
//		if (idpMetadata!=null)
//		{
//			idpRequest.setRecipient( idpMetadata.getEntityId());
//		}
//		idpRequest.setLoa( spRequest.getLoa());
//		idpRequest.setAttributes(spRequest.getAttributes());
//		
//		
//		///
//		/// actualizamos idpMetadata en SM
//		///
//		ObjectMapper objMapper = new ObjectMapper();
//		try
//		{
//			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ idpMetadata:"+idpMetadata.toString());
//			smConnService.updateVariable(sessionId,"idpMetadata",objMapper.writeValueAsString(idpMetadata));
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (updateVariable idpMetadata)  \n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//			log.error(errorMsg);
//		    return "rmError";
//		}
//		
//
//		///
//		// actualizamos idpRequest en SM
//		///
//		objMapper = new ObjectMapper();
//		try
//		{
//			smConnService.updateVariable(sessionId,"idpRequest",objMapper.writeValueAsString(idpRequest));
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (updateVariable idpRequest)  \n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//			log.error(errorMsg);
//		    return "fatalError";
//		}
		
		
		
		
		
		
		////
		////
		////
		
		String tokenToSPms = "";
		tokenToSPms = smConnService.generateToken(sessionId,msName); 
		
		model.addAttribute("msToken", tokenToSPms);
		model.addAttribute("UrlToRedirect", endpoint);
		log.info("En redirectToAP spRequestSource: "+spRequestSource);
		log.info("urlToRedirect 	"+endpoint);
		//return "redirectform";
		
		if (spRequestSource.equalsIgnoreCase("PERSISTENCE"))
		{
			return "redirectform2"; // redirectform2 es GET
		}
		else
		{
			return "redirectform";
		}
	}
	
	private String redirectToSSI(String sessionId, Model model, AttributeSet spRequest, String spRequestSource)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException 
	{
		String msName="";
		String endpoint;
		String apiCall;
		
	
		EntityMetadataList dataMetadatas = cmConnService.getEntityMetadataSet(spRequestSource);
		int size = dataMetadatas.size();
		int selected = 0; //REVIEW
		
		EntityMetadata apMetadata = dataMetadatas.get(0);
	
		///
		// creamos apRequest
		///
		AttributeSet apRequest = new AttributeSet();
		apRequest.setId( UUID.randomUUID().toString());
		apRequest.setType(AttributeSet.TypeEnum.REQUEST);
		apRequest.setIssuer( spRequest.getIssuer());
		apRequest.setProperties( spRequest.getProperties());
		if (apMetadata!=null)
		{
			apRequest.setRecipient( apMetadata.getEntityId());
		}
		apRequest.setLoa( spRequest.getLoa());
		apRequest.setAttributes(spRequest.getAttributes());
	
	
		///
		/// actualizamos apMetadata en SM
		///
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"apMetadata",objMapper.writeValueAsString(apMetadata));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable apMetadata)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError";
		}
	

		///
		// actualizamos apRequest en SM
		///
		objMapper = new ObjectMapper();
		try
		{
			smConnService.updateVariable(sessionId,"apRequest",objMapper.writeValueAsString(apRequest));
		}
		catch (Exception ex)
		{
			String errorMsg= "Exception calling SM (updateVariable apRequest)  \n";
			errorMsg += "Exception message:"+ex.getMessage()+"\n";
			//model.addAttribute("ErrorMessage",errorMsg);
			log.error(errorMsg);
	        return "rmError";
		}
	
		apiCall = "query";
		msName = "SSI-IdP";
		endpoint = getEndpoint(apiCall, msName);
		
		String tokenToSPms = "";
		tokenToSPms = smConnService.generateToken(sessionId,msName); 
		
		model.addAttribute("msToken", tokenToSPms);
		model.addAttribute("UrlToRedirect", endpoint);
		log.info("En redirectToSSI spRequestSource: "+spRequestSource);
		log.info("urlToRedirect 	"+endpoint);
		return "redirectform"; // redirectform es POST
	}
	
	
	///De Response
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
