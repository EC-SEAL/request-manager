package eu.atos.seal.rm.service.param;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import com.nimbusds.jose.JWSAlgorithm;

/**
 *
 * @author UAegean
 */
public interface KeyStoreService {

	//public Key getJWEKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException;
    public Key getHttpSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,UnsupportedEncodingException;
    //public Key getJWTSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,UnsupportedEncodingException;   
    //public Key getJWTPublicKey() throws KeyStoreException, UnsupportedEncodingException;
    public Key getHttpSigPublicKey() throws KeyStoreException, UnsupportedEncodingException;
    public JWSAlgorithm getAlgorithm();
    public int getNumAttempts();
}