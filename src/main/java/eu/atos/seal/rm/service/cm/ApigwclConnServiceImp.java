package eu.atos.seal.rm.service.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.seal.rm.model.AttributeSet;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.model.EntityMetadataPairList;
import eu.atos.seal.rm.service.network.NetworkServiceImpl;
import eu.atos.seal.rm.service.param.KeyStoreService;
import eu.atos.seal.rm.service.param.ParameterService;
import javafx.util.Pair;

//import org.apache.commons.lang3.tuple.Pair;

@Service
public class ApigwclConnServiceImp implements ApigwclController
{
	private static final Logger log = LoggerFactory.getLogger(ApigwclConnServiceImp.class);

	private ParameterService paramServ;
	private KeyStoreService keyStoreService;
	
	private NetworkServiceImpl network = null;
	
	private final String apigwUrl;
	private final String sGetClList="/cl/list/";
	
	public ApigwclConnServiceImp(ParameterService paramServ, KeyStoreService keyStoreServ)
	{
		this.paramServ = paramServ;
		apigwUrl = this.paramServ.getParam("APIGWCL_URL");
	    
	    this.keyStoreService = keyStoreServ;
	}




	// /metadata/externalEntities
	//https://vm.project-seal.eu:9053
	//https://vm.project-seal.eu:9053/cl/list/PERSISTENCE
	// /cl/list/{collection}
	@Override
	public EntityMetadataPairList getCollectionList(String collectionName)
	{
		// returns available **collections**	
		//EntityMetadataList result = null;
		//ArrayList<Pair<String, EntityMetadata>> result = null;
		EntityMetadataPairList result = null;
		try
		{
			if (network == null)
			{
					network = new NetworkServiceImpl(keyStoreService);
			}
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add( new NameValuePair("collection",collectionName));
			
			String jsonResult = network.sendGetURIParams (apigwUrl, 
					//sGetClList + "{" + collectionName + "}", //"collection"
					sGetClList + "{collection}",
					urlParameters, 1);
			
			System.out.println("jsonResult"+jsonResult);
			if (jsonResult != null) {
				//log.info("Result: "+ jsonResult);
		        //ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        //result = mapper.readValue(jsonResult, EntityMetadataList.class);
		        
		        //result =(new ObjectMapper()).readValue(jsonResult,ArrayList.class);
				result =(new ObjectMapper()).readValue(jsonResult,EntityMetadataPairList.class);
			}
			
		}
		catch (Exception e) {
			log.error("APIGW exception 1", e);
			return null;
		}
		
//		try 
//		{
//			RestTemplate restTemplate = new RestTemplate();
//			Map<String, String> uriVariables = new HashMap<>();
//			uriVariables.put("collection", collectionName);
//			String url = apigwUrl + sGetClList + "{" +"collection"+ "}";
//			System.out.println("[getCollectionList] url:"+url);
//			result = restTemplate.getForObject( url , EntityMetadataList.class, uriVariables);
//	
//		}
//		catch (Exception e) {
//			log.error("APIGW exception 2 ", e);
//			return null;
//		}
		
		return result;
	} 
}
