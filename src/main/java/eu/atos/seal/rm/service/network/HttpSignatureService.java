package eu.atos.seal.rm.service.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
/**
 *
 * @author UAegean, Atos
 */
public interface HttpSignatureService {

    public String generateSignature(String hostUrl, String method, String uri, Object postParams, String contentType, String requestId)
            throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, UnsupportedEncodingException, IOException, UnsupportedEncodingException;
    
    //public HttpResponseEnum verifySignature(HttpServletRequest httpRequest, Optional<PublicKey> publicKeyToCheckWith) ;
    
 
}