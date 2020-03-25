package eu.atos.seal.rm.service.network;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

/**
 *
 * @author UAegean, Atos
 */
public class HttpSignatureServiceImpl implements HttpSignatureService {

    private final static Logger log = LoggerFactory.getLogger(HttpSignatureServiceImpl.class);

    public static String[] requiredHeaders = {"(request-target)", "host", "original-date", "digest", "x-request-id"};
    
    private String keyId;
    private Key siginingKey;

    public HttpSignatureServiceImpl(String keyId, Key signingKey)
            throws InvalidKeySpecException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        try {
            this.keyId = keyId;
            this.siginingKey = signingKey;
            //this.signer = new Signer(this.siginingKey, new Signature(this.keyId, algorithm, null, "(request-target)", "host", "original-date", "digest", "x-request-id"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String generateSignature(String hostUrl, String method, String uri, Object postParams, String contentType, String requestId)
            throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, UnsupportedEncodingException, IOException {
    	
    	//log.info("Generate Signature ...");
    	
        final Map<String, String> signatureHeaders = new HashMap<>();
        signatureHeaders.put("host", hostUrl);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM YYYY HH:mm:ss z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        
//        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM YYYY HH:mm:ss z");
        
//        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM YYYY HH:mm:ss z",Locale.ENGLISH);
        
        String nowDate = formatter.format(date);
        signatureHeaders.put("original-date", nowDate);
        signatureHeaders.put("Content-Type", contentType);

        byte[] digest;
        if (postParams != null && contentType.contains("application/json")) {
            ObjectMapper mapper = new ObjectMapper();
            String updateString = mapper.writeValueAsString(postParams);
            digest = MessageDigest.getInstance("SHA-256").digest(updateString.getBytes());
        } else {
            if (postParams != null && contentType.contains("x-www-form-urlencoded") && postParams instanceof Map) {
                digest = MessageDigest.getInstance("SHA-256").digest(getParamsString((Map<String, String>) postParams).getBytes());
            } else {
                digest = MessageDigest.getInstance("SHA-256").digest("".getBytes());
            }
        }
        signatureHeaders.put("digest", "SHA-256=" + new String(org.tomitribe.auth.signatures.Base64.encodeBase64(digest)));
        signatureHeaders.put("Accept", "*/*");
        signatureHeaders.put("Content-Length", Integer.toString(digest.length));
        signatureHeaders.put("x-request-id", requestId);
        signatureHeaders.put("(request-target)", method + " " + uri);

        Algorithm algorithm = Algorithm.RSA_SHA256;
        Signer signer = new Signer(this.siginingKey, new Signature(this.keyId, algorithm, null, "(request-target)", "host", "original-date", "digest", "x-request-id"));
        Signature signed = signer.sign(method, uri, signatureHeaders); //getSigner(this.siginingKey, this.keyId).sign(method, uri, signatureHeaders);
        return signed.toString();
    }

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }
        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }


}