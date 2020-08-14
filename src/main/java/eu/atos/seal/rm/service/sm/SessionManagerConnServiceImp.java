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


@Service
public class SessionManagerConnServiceImp implements SessionManagerConnService
{
	
	private static final Logger log = LoggerFactory.getLogger(SessionManagerConnServiceImp.class);
	//final String hostURL = "http://5.79.83.118:8090";
	//final String hostURL = "http://SessionManager:8080";
	
	//@Value("${gateway.sm.host}")
	private final String hostURL;
	
	//private HttpSignatureServiceImpl httpSigService = null;
	private NetworkServiceImpl network = null;
	
	
	private SessionMngrResponse lastSMResponse = null;
	private KeyStoreService keyStoreService = null;
	private ConfMngrConnService confMngrService = null;
	
	private String sender ="";
	
//	@Autowired
//	ParameterService paramServ;
	
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
		
		//sender = "RMms001";
		
	}
	
	@Override
	public String startSession() throws UnrecoverableKeyException, KeyStoreException, 
										FileNotFoundException, NoSuchAlgorithmException, 
										CertificateException, InvalidKeySpecException, IOException 
	{
		String service = "/sm/startSession";
		log.info("En startSession host:"+hostURL+" service:"+service );
		
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
		if (network == null)
		{
			//network = new NetworkServiceImpl(httpSigService);
			network = new NetworkServiceImpl(keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		
		SessionMngrResponse smResponse = network.sendPostFormSMResponse(hostURL, service, urlParameters,1 );
		
		if (smResponse != null)
		{
		System.out.println("SMresponse(startSession):" +smResponse.toString());
		System.out.println("sessionID:"+smResponse.getSessionData().getSessionId());
		
		}
		else
		{
			return "";
		}
		setLastSMResponse(smResponse);
		return smResponse.getSessionData().getSessionId();
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
		log.info("En generateToken host:"+hostURL+" service:"+service );
		
		
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
		if (network == null)
		{
			//network = new NetworkServiceImpl(httpSigService);
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));
        urlParameters.add(new NameValuePair("sender", sender));   //[TODO] sender en properties
        urlParameters.add(new NameValuePair("receiver", receiver)); //[TODO] en un parametro�? Consultar
        urlParameters.add(new NameValuePair("data", "extraData"));
        log.info("En generateToken sender:"+sender+" receiver:"+receiver );
        
        SessionMngrResponse smResponse = network.sendGetSMResponse(hostURL, service, urlParameters,1);
        
        String additionalData="";
        //System.out.println("SMresponse(generateToken):" +smResponse.toString());
        if ( smResponse.getCode()== ResponseCode.NEW)
        {
	        System.out.println( "addDAta:"+ smResponse.getAdditionalData());
	        additionalData = smResponse.getAdditionalData();
	    }
        setLastSMResponse(smResponse);
        return additionalData; //Devuelve un token
	}
	
	
	@Override
	public String validateToken(String token) throws UnrecoverableKeyException, KeyStoreException, 
													 FileNotFoundException, NoSuchAlgorithmException, 
													 CertificateException, InvalidKeySpecException, IOException 
	{
		String service = "/sm/validateToken";
		log.info("En validateToken host:"+hostURL+" service:"+service );
		
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
		if (network == null)
		{
			//network = new NetworkServiceImpl(httpSigService);
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		
		String sessionID = "";
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(
	    		new NameValuePair("token",token));
	    
	    SessionMngrResponse smResponse = null;
	   
	    System.out.println("Enviando validateToken :"+token);
	    //response = network.sendGet(hostURL, service, urlParameters);
	    smResponse = network.sendGetSMResponse(hostURL, service, urlParameters,1);
	    
	    if ( smResponse.getCode()== ResponseCode.OK)
	    {
	    	sessionID = smResponse.getSessionData().getSessionId();
	    	System.out.println("SessionID:"+sessionID);
	    }
		// else   // Si hay error p.ej. JWT is blacklisted �q hacemos?
		System.out.println("validateToken smResponse:"+smResponse);
		setLastSMResponse(smResponse);
		return sessionID; //devuelve un sessionId
	}
	
	
	
	@Override
	public HashMap<String, Object> readVariables(String sessionId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/getSessionData";
		log.info("En readVariables host:"+hostURL+" service:"+service );
		
		HashMap<String, Object> sessionVbles= new HashMap<String, Object>();
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
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
	    }
	    setLastSMResponse(smResponse);
	    return sessionVbles;
	}
	
	
	
	@Override
	public Object readVariable(String sessionId, String variableName) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/getSessionData";
		log.info("En readVariable host:"+hostURL+" service:"+service );
		
		
		HashMap<String, Object> sessionVbles = new HashMap<String, Object>();
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
		if (network == null)
		{
			network = new NetworkServiceImpl(this.keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    urlParameters.add(new NameValuePair("variableName",variableName));
	    log.info("En readVariable sessionId:"+sessionId+" variableName:"+variableName );
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	System.out.println("Enviando getSessionData");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);
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
	    	
	    	System.out.println( "sessionVbles:"+sessionVbles.get("spRequest"));
//	    	//AttributeSet spRequest = (AttributeSet) sessionVbles.get("spRequest");
//	    	ObjectMapper objectMapper = new ObjectMapper();
//	    	
//	    	AttributeSet spRequest = objectMapper.readValue(sessionVbles.get("spRequest").toString(), AttributeSet.class);
//	    	System.out.println("spRequest.issuer"+spRequest.getIssuer());
	    }
	    
	    setLastSMResponse(smResponse);
	    //[TODO] �que devolvemos?
	    return sessionVbles.get(variableName);
	}
	

	// Returns the list of dataSet/linkRequest objects from the DataStore.
	// If type is null, returns the complete DataStore.
	@Override
	public Object readDS(String sessionId, String type) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		//String service = "/sm/new/get";
		String service = "/sm/new/search";
		
		Object sessionVble = new Object();
		
		if (network == null)
		{
				network = new NetworkServiceImpl(keyStoreService);
		}
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    urlParameters.add(new NameValuePair("type",type));
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	log.info("Sending new/search ...");
	    	smResponse = network.sendGetSMResponse(hostURL, service, urlParameters, 1);
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
	    }
	    
	    
	    return sessionVble;
	}
	
	@Override
	public void updateVariable(String sessionId, String varName, String varValue)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException {
		String service = "/sm/updateSessionData";
		log.info("En updateVariable("+varName+") host:"+hostURL+" service:"+service );
		
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
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
        System.out.println("Response updateSessionData"+smResponse);
		
	}

	@Override
	public void deleteSession(String sessionId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException		// /sm/endSession
	{
		String service = "/sm/endSession";
		log.info("En deleteSession host:"+hostURL+" service:"+service );
		
//		if (httpSigService== null)
//		{
//			createHttpSigService();
//		}
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
	    System.out.println("Response endSession :<"+smResponse.toString()+">");
		
	}

//	@Override
//	public String getSession(String varName, String varValue)
//	{
//		// TODO Auto-generated method stub
//		return null;
//		
//	}

	
	///
	/// PRIVATE
	///
	
	//[TODO] Leer de variables de entorno
//	private void createHttpSigService() throws KeyStoreException, FileNotFoundException, IOException,
//											   NoSuchAlgorithmException, CertificateException, 
//											   UnrecoverableKeyException, InvalidKeySpecException 
//	{
//		//[TODO] Cambiar para cada microservicio, el su
//		String fingerPrint = "7a9ba747ab5ac50e640a07d90611ce612b7bde775457f2e57b804517a87c813b";
//		ClassLoader classLoader = getClass().getClassLoader();
//		//String path = classLoader.getResource("testKeys/keystore.jks").getPath();
//		ClassPathResource resource = new ClassPathResource("testKeys/keystore.jks");
//		
//		
//		log.info("En createHttpSigService resource:"+resource.getPath());
//		InputStream certIS = resource.getInputStream();
//		//File jwtCertFile = ResourceUtils.getFile("classpath:testKeys/keystore.jks");
//		
//		
//		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//		//File jwtCertFile = new File(path);
//		//InputStream certIS = new FileInputStream(jwtCertFile);
//		keystore.load(certIS, "keystorepass".toCharArray());
//		
//		Key signingKey = keystore.getKey("1", "selfsignedpass".toCharArray());
//		
//		httpSigService = new HttpSignatureServiceImpl(fingerPrint, signingKey);
//	}

//	@Override
//	public SessionMngrResponse getLastSMResponse() {
//		return lastSMResponse;
//	}
//
	private void setLastSMResponse(SessionMngrResponse lastSMResponse) {
		this.lastSMResponse = lastSMResponse;
	}

//	@Override
//	public String generateToken(String sessionId, String receiver)
//			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
//			CertificateException, InvalidKeySpecException, IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}