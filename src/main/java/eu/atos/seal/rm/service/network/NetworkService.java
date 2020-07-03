package eu.atos.seal.rm.service.network;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;

import eu.atos.seal.rm.model.SessionMngrResponse;

public interface NetworkService 
{
	public String sendGetURIParams(String hostUrl, String uri, List<NameValuePair> urlParameters, int attempt) throws IOException, NoSuchAlgorithmException;

    public String sendGet(String hostUrl, String uri, List<NameValuePair> urlParameters, int attempt) throws IOException, NoSuchAlgorithmException;

    public String sendPostForm(String hostUrl, String uri, List<NameValuePair> urlParameters, int attempt) throws IOException, NoSuchAlgorithmException;

    public String sendPostBody(String hostUrl, String uri, Object postBody, String contentType, int attempt) throws IOException, NoSuchAlgorithmException;


	public SessionMngrResponse sendPostFormSMResponse(String hostUrl, String uri, 
													  List<NameValuePair> urlParameters, int attempt)
			throws IOException, NoSuchAlgorithmException;
	
	public SessionMngrResponse sendGetSMResponse(String hostUrl, String uri, 
												 List<NameValuePair> urlParameters, int attempt) 
			throws IOException, NoSuchAlgorithmException;

	public SessionMngrResponse sendPostBodySMResponse(String hostUrl, String uri, Object postBody, String contentType,
			int attempt) throws IOException, NoSuchAlgorithmException;
}
