package eu.atos.seal.rm.service.cm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.AttributeTypeList;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.model.MsMetadataList;
import eu.atos.seal.rm.service.network.NetworkServiceImpl;
import eu.atos.seal.rm.service.param.KeyStoreService;
import eu.atos.seal.rm.service.param.ParameterService;

@Service
public class ConfMngrConnServiceImp implements ConfMngrConnService
{
	private static final Logger log = LoggerFactory.getLogger(ConfMngrConnServiceImp.class);

	private ParameterService paramServ;
	private KeyStoreService keyStoreService;
	
	private NetworkServiceImpl network = null;
	
	private final String cmUrl;
		
	@Value("${apigwcl.cm.getExternalEntitiesPath}")
	private String getExternalEntitiesPath;
	
	@Value("${apigwcl.cm.getEntityMetadataSetPath}")
	private String[] getEntityMetadataSetPath;
	
	@Value("${apigwcl.cm.getEntityMetadataPath}")
	private String[] getEntityMetadataPath;
	
	@Value("${apigwcl.cm.getAllMicroservicesPath}")
	private String getAllMicroservicesPath;
	
	@Value("${apigwcl.cm.getMicroservicesByApiClassPath}")
	private String[] getMicroservicesByApiClassPath;
	
	@Value("${apigwcl.cm.getInternalsPath}")
	private String getInternalsPath;
	
	@Value("${apigwcl.cm.getConfigurationPath}")
	private String[] getConfigurationPath;
	
	@Value("${apigwcl.cm.getAttributeSetByProfilePath}")
	private String[] getAttributeSetByProfilePath;
	
	@Value("${apigwcl.cm.getAttributeProfilesPath}")
	private String getAttributeProfilesPath;
	
	
	

	
	@Autowired
    public ConfMngrConnServiceImp (ParameterService paramServ, KeyStoreService keyStoreServ) {
		this.paramServ = paramServ;
        cmUrl = this.paramServ.getParam("CONFIGURATION_MANAGER_URL");
        
        this.keyStoreService = keyStoreServ;
	}
	
	

	
	// /metadata/externalEntities
	@Override
	public List<String> getExternalEntities () {
		
		// returns available **collections**
		
		List<String> result = null;
		
		try {
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			String jsonResult = network.sendGet (cmUrl, 
					getExternalEntitiesPath, 
					urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("Result: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, List.class);
			}
			
		}
		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		List<String> result;
//		
//		try {
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/externalEntities/"
//												getExternalEntitiesPath, List.class);
//		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	} 
	
	// /metadata/externalEntities/{collectionId}
	@Override
	public EntityMetadataList getEntityMetadataSet (String collectionId)
	{
		
		EntityMetadataList result = null;
		
		try {
		
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new NameValuePair(getEntityMetadataSetPath[1], collectionId));
			String jsonResult = network.sendGetURIParams (cmUrl, 
					getEntityMetadataSetPath[0] + "{" + getEntityMetadataSetPath[1] + "}", 
					urlParameters, 1);
						
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, EntityMetadataList.class);
		        //log.info("Result: "+ result);
			}
		
		}
		
//		Map<String, String> uriVariables = new HashMap<>();
//		uriVariables.put(getEntityMetadataSetPath[1], collectionId);
//		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		EntityMetadataList result;
//		
//		try { 
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/externalEntities/{collectionId}"
//												getEntityMetadataSetPath[0] + "{" + getEntityMetadataSetPath[1] + "}" , 
//												EntityMetadataList.class, uriVariables);
//		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	}
	
	// /metadata/externalEntities/{collectionId}/{entityId}
	@Override
	public EntityMetadata getEntityMetadata (String collectionId, String entityId){
		
		EntityMetadata result = null;
		
		try {
		
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new NameValuePair(getEntityMetadataPath[1], collectionId));
			urlParameters.add(new NameValuePair(getEntityMetadataPath[2], entityId));
			String jsonResult = network.sendGetURIParams (cmUrl, 
					getEntityMetadataPath[0] + "{" + getEntityMetadataPath[1] + "}" + "/{" + getEntityMetadataPath[2] + "}", 
					urlParameters, 1);
						
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, EntityMetadata.class);
			}
		
		}
		
//		Map<String, String> uriVariables = new HashMap<>();
//		uriVariables.put(getEntityMetadataPath[1], collectionId);
//		uriVariables.put(getEntityMetadataPath[2], entityId);
//		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		EntityMetadata result;
//		
//		try {
//			result = restTemplate.getForObject(cmUrl + 		//"http://localhost:8080/cm/metadata/externalEntities/{collectionId}/{entityId}"
//												getEntityMetadataPath[0] + "{" + getEntityMetadataPath[1] + "}"+ "/{" + getEntityMetadataPath[2] + "}" , 
//												EntityMetadata.class, uriVariables);
//		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	}
	
	
	
	
	// /metadata/microservices
	@Override
	public MsMetadataList getAllMicroservices (){
		
		MsMetadataList result = null;
		
		try {
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			String jsonResult = network.sendGet (cmUrl, 
					getAllMicroservicesPath, 
					urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, MsMetadataList.class);
		        //log.info("Result: "+ result);
			}
			
		}

//		RestTemplate restTemplate = new RestTemplate();
//		
//		MsMetadataList result;
//		
//		try {
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/microservices/"
//												getAllMicroservicesPath, MsMetadataList.class);
//		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}

		return result;
	}
	
	// /metadata/microservices/{apiClass}
	@Override
	public MsMetadataList getMicroservicesByApiClass (String apiClasses)//throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		// input like "SP, IDP, AP, GW, ACM, SM, CM"
		
		MsMetadataList result = null;
		
		try {
		
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new NameValuePair(getMicroservicesByApiClassPath[1], apiClasses));
			String jsonResult = network.sendGetURIParams (cmUrl, 
					getMicroservicesByApiClassPath[0] + "{" + getMicroservicesByApiClassPath[1] + "}", 
					urlParameters, 1);
			
	//		String jsonResult = network.sendGet (cmUrl, 
	//				getMicroservicesByApiClassPath[0] + apiClasses, 
	//				urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, MsMetadataList.class);
		        //log.info("Result: "+ result);
			}
		
//		Map<String, String> uriVariables = new HashMap<>();
//		uriVariables.put("apiClass", apiClasses);
//		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		
//		
//		try {
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/microservices/{apiClass}"
//												getMicroservicesByApiClassPath[0] + "{" + getMicroservicesByApiClassPath[1] + "}" ,
//												MsMetadataList.class, uriVariables);
		
		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	} 
	
	// /metadata/internal
	@Override
	public List<String> getInternalConfs () {
		
		// returns available **internal configurations**
		
		List<String> result = null;
		
		try {
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			String jsonResult = network.sendGet (cmUrl, 
					getInternalsPath, 
					urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, List.class);
		        //log.info("Result: "+ result);
			}
			
		}
		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		List<String> result;
//		
//		try {
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/internal/"
//												getInternalsPath, List.class);
//		}
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	} 
	
	// /metadata/internal/{confId}
	@Override
	public EntityMetadata getConfiguration (String confId)
	{
		EntityMetadata result = null;
		
		try {
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new NameValuePair(getConfigurationPath[1], confId));
			String jsonResult = network.sendGetURIParams (cmUrl, 
					getConfigurationPath[0] + "{" + getConfigurationPath[1] + "}", 
					urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, EntityMetadata.class);
		        //log.info("Result: "+ result);
			}
			
		}
		
//		Map<String, String> uriVariables = new HashMap<>();
//		uriVariables.put(getConfigurationPath[1], confId);
//		
//		RestTemplate restTemplate = new RestTemplate();
//		
//		EntityMetadata result;
//		
//		try { 
//			result = restTemplate.getForObject( cmUrl + 	//"http://localhost:8080/cm/metadata/internal/{confId}"
//												getConfigurationPath[0] + "{" + getConfigurationPath[1] + "}" , 
//												EntityMetadata.class, uriVariables);
//		}
		
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	}



	// /metadata/attributes/
	@Override
	public List<String> getAttributeProfiles() {
		// returns available **attribute profiles**
		
				List<String> result = null;
				
				try {
					if (network == null)
					{
							network = new NetworkServiceImpl(keyStoreService);
					}
					List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
					
					String jsonResult = network.sendGet (cmUrl, 
							getAttributeProfilesPath, 
							urlParameters, 1);
					
					if (jsonResult != null) {
						//log.info("Result: "+ jsonResult);
				        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				        result = mapper.readValue(jsonResult, List.class);
				        log.info("Result: "+ result);
					}
					
				}

				catch (Exception e) {
					log.error("CM exception", e);
					return null;
				}
				
				return result;
	}



	// /metadata/attributes/{attrProfileId}
	@Override
	public AttributeTypeList getAttributeSetByProfile(String profileId) {
		
		AttributeTypeList result = null;
		
		try {
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new NameValuePair(getAttributeSetByProfilePath[1], profileId));
			String jsonResult = network.sendGetURIParams (cmUrl, 
					getAttributeSetByProfilePath[0] + "{" + getAttributeSetByProfilePath[1] + "}", 
					urlParameters, 1);
			
			if (jsonResult != null) {
				//log.info("jsonResult: "+ jsonResult);
		        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        result = mapper.readValue(jsonResult, AttributeTypeList.class);
		        log.info("Result: "+ result);
			}
			
		}
		
		catch (Exception e) {
			log.error("CM exception", e);
			return null;
		}
		
		return result;
	}
	
	
}