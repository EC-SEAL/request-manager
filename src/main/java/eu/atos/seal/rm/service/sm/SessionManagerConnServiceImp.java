package eu.atos.seal.rm.service.sm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import eu.atos.seal.rm.model.ResponseCode;
import eu.atos.seal.rm.model.SessionMngrResponse;
import eu.atos.seal.rm.model.UpdateDataRequest;
import eu.atos.seal.rm.service.cm.ConfMngrConnService;
import eu.atos.seal.rm.service.network.NetworkServiceImpl;
import eu.atos.seal.rm.service.param.KeyStoreService;
import eu.atos.seal.rm.service.param.ParameterService;
import eu.atos.seal.rm.model.RequestParameters;


@Service
public class SessionManagerConnServiceImp implements SessionManagerConnService
{
	
	private static final Logger log = LoggerFactory.getLogger(SessionManagerConnServiceImp.class);
	private final String hostURL;
	
	private NetworkServiceImpl network = null;
	
	private SessionMngrResponse lastSMResponse = null;  // What for??
	private KeyStoreService keyStoreService = null;
	private ConfMngrConnService confMngrService = null;
	
	private String sender ="";
	
	@Autowired
	public SessionManagerConnServiceImp(ConfMngrConnService confMngrConnService,KeyStoreService keyStoreServ,ParameterService paramServ)
	{
		hostURL = paramServ.getParam("SESSION_MANAGER_URL");
		this.keyStoreService = keyStoreServ;
		this.confMngrService = confMngrConnService;
		//EntityMetadata myLGW = null;
		
		String thisCL = confMngrService.getMicroservicesByApiClass("RM").get(0).getMsId(); // The unique client
		if (thisCL != null)
		{
        	sender = thisCL;
		}
        else
        {
        	sender = "RMms001";
        	log.error("HARDCODED sender! "+ sender);
		}	
	}
	
	@Override
	public String startSession() throws UnrecoverableKeyException, KeyStoreException, 
										FileNotFoundException, NoSuchAlgorithmException, 
										CertificateException, InvalidKeySpecException, IOException 
	{
		String service = "/sm/startSession";
		log.info("StartSession host: "+hostURL+" service: "+service );
		
		if (network == null)
		{
			//network = new NetworkServiceImpl(httpSigService);
			network = new NetworkServiceImpl(keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		
		SessionMngrResponse smResponse = network.sendPostFormSMResponse(hostURL, service, urlParameters,1 );
		
		if (smResponse != null)
		{
//			System.out.println("SMresponse(startSession):" +smResponse.toString());
//			System.out.println("sessionID:"+smResponse.getSessionData().getSessionId());
			setLastSMResponse(smResponse);
			return smResponse.getSessionData().getSessionId();	
		}
		else return "";
		
	}
	
	@Override
	public String getSession(String varName, String varValue)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String generateToken(String sessionId, String receiver)
	//(String sessionId, String sender, String receiver)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/generateToken";
		log.info("GenerateToken host:"+hostURL+" service:"+service );
		
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));
        urlParameters.add(new NameValuePair("sender", sender));   
        urlParameters.add(new NameValuePair("receiver", receiver));
        urlParameters.add(new NameValuePair("data", "extraData"));
        log.info("En generateToken sender:"+sender+" receiver:"+receiver );
        
        SessionMngrResponse smResponse = network.sendGetSMResponse(hostURL, service, urlParameters,1);
        
        String additionalData="";
        //System.out.println("SMresponse(generateToken):" +smResponse.toString());
        if ( smResponse.getCode()== ResponseCode.NEW)
        {
	        log.info( "addDAta:"+ smResponse.getAdditionalData());
	        additionalData = smResponse.getAdditionalData();
	    }
        setLastSMResponse(smResponse);
        return additionalData; // returns a token
	}
	
	
	@Override
	public String validateToken(String token) throws UnrecoverableKeyException, KeyStoreException, 
													 FileNotFoundException, NoSuchAlgorithmException, 
													 CertificateException, InvalidKeySpecException, IOException 
	{
		String service = "/sm/validateToken";
		log.info("ValidateToken host:"+hostURL+" service:"+service );
		
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		
		String sessionID = "";
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(
	    		new NameValuePair("token",token));
	    
	    SessionMngrResponse smResponse = null;
	   
	    System.out.println("Sending validateToken :"+token);
	    smResponse = network.sendGetSMResponse(hostURL, service, urlParameters,1);
	    
	    if ( smResponse.getCode()== ResponseCode.OK)
	    {
	    	sessionID = smResponse.getSessionData().getSessionId();
//	    	System.out.println("SessionID:"+sessionID);
//	    	System.out.println("validateToken smResponse:"+smResponse);
			setLastSMResponse(smResponse);
			return sessionID; 
	    }
	    else return null;		
	}
	
	
	
	@Override
	public HashMap<String, Object> readVariables(String sessionId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/getSessionData";
		log.info("ReadVariables host:"+hostURL+" service:"+service );
		
		HashMap<String, Object> sessionVbles= new HashMap<String, Object>();

		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	System.out.println("Enviando getSessionData");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = network.sendGetSMResponse(hostURL, service, urlParameters,1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("Response getSessionData:<"+smResponse.toString()+">");
	    if (smResponse.getCode()== ResponseCode.OK)
	    {
	    	sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	setLastSMResponse(smResponse);
		    return sessionVbles;
	    }
	    else return null;
	    
	}
	
	
	
	@Override
	public Object readVariable(String sessionId, String variableName) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/getSessionData";
		log.info("ReadVariable host:"+hostURL+" service:"+service );
		
		
		HashMap<String, Object> sessionVbles = new HashMap<String, Object>();
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    urlParameters.add(new NameValuePair("variableName",variableName));
	    log.info("At readVariable sessionId:"+sessionId+" variableName:"+variableName );
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	//System.out.println("Sending getSessionData");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    log.info("Response getSessionData:<"+smResponse.toString()+">");
	    if (smResponse.getCode()== ResponseCode.OK)
	    {
	    	sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	
	    	log.info( "sessionVbles:"+sessionVbles.get("spRequest"));
//	    	//AttributeSet spRequest = (AttributeSet) sessionVbles.get("spRequest");
//	    	ObjectMapper objectMapper = new ObjectMapper();
//	    	
//	    	AttributeSet spRequest = objectMapper.readValue(sessionVbles.get("spRequest").toString(), AttributeSet.class);
//	    	System.out.println("spRequest.issuer"+spRequest.getIssuer());
	    	
	    	setLastSMResponse(smResponse);
		    
		    return sessionVbles.get(variableName);
	    }
	    else return null;
	}
	

	// Returns the list of dataSet/linkRequest objects from the DataStore.
	// If type is null, returns the complete DataStore.
	@Override
	public Object readDS(String sessionId, String type) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		//String service = "/sm/new/get";
		String service = "/sm/new/search";
		String contentType="application/json";
		
		Object sessionVble = new Object();
		
		if (network == null)
		{
				network = new NetworkServiceImpl(keyStoreService);
		}
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//	    urlParameters.add(new NameValuePair("sessionId",sessionId));
//	    urlParameters.add(new NameValuePair("type",type));
	    
	    RequestParameters requestParameters = new RequestParameters();
        requestParameters.setSessionId(sessionId);
        requestParameters.setType(type);
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	log.info("Sending new/search ...");
	    	//smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);
	    	smResponse = network.sendPostBodySMResponse(hostURL, /*"/sm/new/get"*/ service, requestParameters, contentType, 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    if (smResponse.getCode()== ResponseCode.OK)
	    {
	    	//sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	sessionVble = smResponse.getAdditionalData();
	    	
	    	log.info("DS (only "+ type + " : "+ sessionVble.toString());
	    	return sessionVble;
	    }
	    else return null;
	}
	
	@Override
	public void updateVariable(String sessionId, String varName, String varValue)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException {
		String service = "/sm/updateSessionData";
		log.info("UpdateVariable("+varName+") host:"+hostURL+" service:"+service );
		
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
//		ObjectMapper mapper = new ObjectMapper();
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));
        //urlParameters.add(new NameValuePair("sender", sender));
        
        UpdateDataRequest updateDR = new UpdateDataRequest();
        updateDR.setSessionId(sessionId);
        updateDR.setVariableName(varName);
        updateDR.dataObject(varValue);
//        String postBody = mapper.writeValueAsString(updateDR);
        
        String contentType="application/json";
        SessionMngrResponse smResponse = null;
        
        smResponse = network.sendPostBodySMResponse(hostURL, service, updateDR, contentType, 1);
        
        setLastSMResponse(smResponse);
        log.info("Response updateSessionData"+smResponse);
		
	}

	@Override
	public void deleteSession(String sessionId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException		// /sm/endSession
	{
		String service = "/sm/endSession";
		log.info("En deleteSession host:"+hostURL+" service:"+service );
		
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    
	    SessionMngrResponse smResponse = null;
//	    try {
	    	System.out.println("Enviando endSession");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    setLastSMResponse(smResponse);
	    log.info("Response endSession :<"+smResponse.toString()+">");
		
	}


	private void setLastSMResponse(SessionMngrResponse lastSMResponse) {
		this.lastSMResponse = lastSMResponse;
	}

	
}