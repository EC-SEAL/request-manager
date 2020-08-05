package eu.atos.seal.rm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.AttributeSet;
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
import java.util.List;
import java.util.UUID;

import javax.management.AttributeList;
import javax.servlet.http.HttpSession;

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
	
		
		//[DELETE
		if (spRequestEP==null)
		{
			spRequestEP="auth_request";
		}
		if (spRequestSource==null)
		{
			spRequestSource="eIDAS";
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
			if (spRequestSource.contains("iscovery"))
			{
				return goToSelectApUI(model, spRequest,spMetadata,sourceList);
			}
			else  //Should be eIDAS of eduGAIN
			{
				return prepareAndGoToIdp(sessionId,spRequest,spMetadata,spRequestSource);
			}
		}
		else //data_query or null ¿puede ser null?
		{
			if (spRequestSource.contains("iscovery"))
			{
				return goToSelectApUI(model, spRequest,spMetadata,sourceList);
			}
			else
			{
				return prepareAndGoToAP(sessionId, spRequest,spMetadata,spRequestSource);
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
		
		
		
		
		//return null;
	}
	
	// Falta un método "from UI", lee las variables de sesión del formulario, y llama a prepare and gotoidp, con el spRequestSource fijado".
	

	


	private String prepareAndGoToIdp(String sessionId, AttributeSet spRequest, EntityMetadata spMetadata, String spRequestSource) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
//		System.out.println("getEntityMetadataSet(idp)"+cmConnService.getEntityMetadataSet("IdP").toString());
//		System.out.println("EntityMetadata(EIDAS)"+cmConnService.getEntityMetadata("AUTHSOURCE","EIDAS").toString());
//		System.out.println("EntityMetadata(EDUGAIN)"+cmConnService.getEntityMetadata("AUTHSOURCE","EDUGAIN").toString());
//		
		log.info("En prepareAndGoToIdp");
		
		
		EntityMetadata authMetadata0 = cmConnService.getEntityMetadata("AUTHSOURCE", spRequestSource); // Reading the AUTHSOURCEmetadata.json
		System.out.println("This is the spRequestSource: " + spRequestSource);
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
		EntityMetadataList eMTDList = cmConnService.getEntityMetadataSet("EIDAS");
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
		System.out.println("Create token to "+msName+" tokenValue:"+token);
		System.out.println("redirect to: "+endpoint);
		
		this.model.addAttribute("msToken", token);
		this.model.addAttribute("UrlToRedirect", endpoint);
		
	
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
		
		if (spRequestSource.contains("PDS"))
		{
			spRequestSource="PERSISTENCE";
			apiCall = "load";
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
		
	
		return "redirectform";
	}

	private String goToSelectApUI(Model model, AttributeSet spRequest, EntityMetadata spMetadata, EntityMetadataList sourceList)
	{
		// TODO Auto-generated method stub
		log.info("en goToSelectApUI");
		//Rellenar EntityMetadataList
		// y ponerla en la variable sourceList
		//EntityMetadataList sourceList = new EntityMetadataList();
		session.setAttribute("sourceList",sourceList);
		
		//Rellenar atributos con la spRequest
		// y ponerla en la vble de sesion: attributeRequestList
		AttributeTypeList attributeRequestList = new AttributeTypeList();

		attributeRequestList.addAll(spRequest.getAttributes());
		session.setAttribute("attributesRequestList", attributeRequestList);
		System.out.println("This is  name: " + attributeRequestList.get(0).getName()	);
		System.out.println("This is friendly name: " + attributeRequestList.get(0).getFriendlyName());
		//Rellenar spMetadata
		//con la vble spMetadata
		session.setAttribute("spMetadata", spMetadata);
		
		return "redirect:../request_client";  //REVIEW
	}

	private String goToSelectIdpUI() {
		// TODO Auto-generated method stub
		log.info("en goToSelectIdpUI");
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
	
	private String readSpRequestEP(String sessionId) {
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
}
