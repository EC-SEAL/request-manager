package eu.atos.seal.rm.service.param;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;


/**
 *
 * @author UAegean
 */
@Service
public class KeyStoreServiceImpl implements KeyStoreService {

    private final String certPath;
    private final String keyPass;
    private final String storePass;
    //private final String jwtKeyAlias;
    private final String httpSigKeyAlias;
    //private final String jweKeyAlias;
    
    private final String httpSigAttempts;

    private KeyStore keystore;

    private ParameterService paramServ;
    
    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreServiceImpl.class);


    @Autowired
    public KeyStoreServiceImpl(ParameterService paramServ) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        this.paramServ = paramServ;
        certPath = this.paramServ.getParam("KEYSTORE_PATH");
        keyPass = this.paramServ.getParam("KEY_PASS");
        storePass = this.paramServ.getParam("STORE_PASS");
        //jwtKeyAlias = this.paramServ.getParam("JWT_CERT_ALIAS");
        httpSigKeyAlias = this.paramServ.getParam("HTTPSIG_CERT_ALIAS");
        //jweKeyAlias = this.paramServ.getParam("JWE_CERT_ALIAS");
        
        httpSigAttempts = this.paramServ.getParam("HTTPSIG_ATTEMPTS") == null ? "2": this.paramServ.getParam("HTTPSIG_ATTEMPTS");
        
        LOG.info ("certPath: " + certPath);
//        LOG.info ("jwtKeyAlias: " + jwtKeyAlias);
        LOG.info ("httpSigKeyAlias: " + httpSigKeyAlias);
//        LOG.info ("jweKeyAlias: " + jweKeyAlias);

        keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (!org.springframework.util.StringUtils.isEmpty(paramServ.getParam("ASYNC_SIGNATURE")) && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
            
        	//LOG.info ("ASYNC_SIGNATURE: true");
        	File jwtCertFile = new File(certPath);
            InputStream certIS = new FileInputStream(jwtCertFile);
            keystore.load(certIS, storePass.toCharArray());
            //LOG.info ("keystore: loaded");
        } else {
            //init an empty keystore otherwise an exception is thrown
            keystore.load(null, null);
        }

    }
    
    public int getNumAttempts() {
    	return Integer.parseInt(httpSigAttempts);
    }
    
    public Key getHttpSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
        //"httpsigkey"
        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
        	//LOG.info ("GettingHttpSigningKey....");
        	return keystore.getKey(httpSigKeyAlias, keyPass.toCharArray());
        }
        String secretKey = paramServ.getParam("SIGNING_SECRET");
        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
    }
 /*   
    public Key getJWTSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
        //"jwtKeyAlias"
        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
        	//LOG.info ("GettingJWTKey....");
        	return keystore.getKey(jwtKeyAlias, keyPass.toCharArray());
        }
        String secretKey = paramServ.getParam("SIGNING_SECRET");
        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
    }

    public Key getJWEKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
        //"jweKeyAlias"
        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
            return keystore.getKey(jweKeyAlias, keyPass.toCharArray());
        }
        String secretKey = paramServ.getParam("SIGNING_SECRET");
        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
    }

    public Key getJWTPublicKey() throws KeyStoreException, UnsupportedEncodingException {
        //"jwtkey"
        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
            Certificate cert = keystore.getCertificate(jwtKeyAlias);
            return cert.getPublicKey();
        }
        String secretKey = paramServ.getParam("SIGNING_SECRET");
        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
    }
*/
    public Key getHttpSigPublicKey() throws KeyStoreException, UnsupportedEncodingException {
        //"httpSignaturesAlias"
        Certificate cert = keystore.getCertificate(httpSigKeyAlias);
        return cert.getPublicKey();

    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

    public ParameterService getParamServ() {
        return paramServ;
    }

    public void setParamServ(ParameterService paramServ) {
        this.paramServ = paramServ;
    }

    @Override
    public JWSAlgorithm getAlgorithm() {
        if (!org.springframework.util.StringUtils.isEmpty(paramServ.getParam("ASYNC_SIGNATURE")) && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
            return JWSAlgorithm.RS256;
        }
        return JWSAlgorithm.HS256;
    }

}

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.security.Key;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.nimbusds.jose.JWSAlgorithm;
//
//
///**
// *
// * @author UAegean
// */
//@Service
//public class KeyStoreServiceImpl implements KeyStoreService {
//
//    private final String certPath;
//    private final String keyPass;
//    private final String storePass;
//    //private final String jwtKeyAlias;
//    private final String httpSigKeyAlias;
//    //private final String jweKeyAlias;
//    
//    private final String httpSigAttempts;
//
//    private KeyStore keystore;
//
//    private ParameterService paramServ;
//    
//    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreServiceImpl.class);
//
//
//    @Autowired
//    public KeyStoreServiceImpl(ParameterService paramServ) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
//        this.paramServ = paramServ;
//        certPath = this.paramServ.getParam("KEYSTORE_PATH");
//        keyPass = this.paramServ.getParam("KEY_PASS");
//        storePass = this.paramServ.getParam("STORE_PASS");
//        //jwtKeyAlias = this.paramServ.getParam("JWT_CERT_ALIAS");
//        httpSigKeyAlias = this.paramServ.getParam("HTTPSIG_CERT_ALIAS");
//        //jweKeyAlias = this.paramServ.getParam("JWE_CERT_ALIAS");
//        
//        httpSigAttempts = this.paramServ.getParam("HTTPSIG_ATTEMPTS") == null ? "2": this.paramServ.getParam("HTTPSIG_ATTEMPTS");
//        
//        LOG.info ("certPath: " + certPath);
////        LOG.info ("jwtKeyAlias: " + jwtKeyAlias);
//        LOG.info ("httpSigKeyAlias: " + httpSigKeyAlias);
////        LOG.info ("jweKeyAlias: " + jweKeyAlias);
//
//        keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//        if (!org.springframework.util.StringUtils.isEmpty(paramServ.getParam("ASYNC_SIGNATURE")) && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
//            
//        	//LOG.info ("ASYNC_SIGNATURE: true");
//        	File jwtCertFile = new File(certPath);
//            InputStream certIS = new FileInputStream(jwtCertFile);
//            keystore.load(certIS, storePass.toCharArray());
//            //LOG.info ("keystore: loaded");
//        } else {
//            //init an empty keystore otherwise an exception is thrown
//            keystore.load(null, null);
//        }
//
//    }
//    
//    public int getNumAttempts() {
//    	return Integer.parseInt(httpSigAttempts);
//    }
//    
//    public Key getHttpSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
//        //"httpsigkey"
//        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
//        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
//        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
//        	//LOG.info ("GettingHttpSigningKey....");
//        	return keystore.getKey(httpSigKeyAlias, keyPass.toCharArray());
//        }
//        String secretKey = paramServ.getParam("SIGNING_SECRET");
//        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
//    }
// /*   
//    public Key getJWTSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
//        //"jwtKeyAlias"
//        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
//        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
//        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
//        	//LOG.info ("GettingJWTKey....");
//        	return keystore.getKey(jwtKeyAlias, keyPass.toCharArray());
//        }
//        String secretKey = paramServ.getParam("SIGNING_SECRET");
//        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
//    }
//
//    public Key getJWEKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException {
//        //"jweKeyAlias"
//        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
//        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
//        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
//            return keystore.getKey(jweKeyAlias, keyPass.toCharArray());
//        }
//        String secretKey = paramServ.getParam("SIGNING_SECRET");
//        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
//    }
//
//    public Key getJWTPublicKey() throws KeyStoreException, UnsupportedEncodingException {
//        //"jwtkey"
//        String asyncSignature = paramServ.getParam("ASYNC_SIGNATURE");
//        if (!org.springframework.util.StringUtils.isEmpty(asyncSignature) && Boolean.valueOf(asyncSignature)) {
//            Certificate cert = keystore.getCertificate(jwtKeyAlias);
//            return cert.getPublicKey();
//        }
//        String secretKey = paramServ.getParam("SIGNING_SECRET");
//        return new SecretKeySpec(secretKey.getBytes("UTF-8"), 0, secretKey.length(), "HmacSHA256");
//    }
//*/
//    public Key getHttpSigPublicKey() throws KeyStoreException, UnsupportedEncodingException {
//        //"httpSignaturesAlias"
//        Certificate cert = keystore.getCertificate(httpSigKeyAlias);
//        return cert.getPublicKey();
//
//    }
//
//    public KeyStore getKeystore() {
//        return keystore;
//    }
//
//    public void setKeystore(KeyStore keystore) {
//        this.keystore = keystore;
//    }
//
//    public ParameterService getParamServ() {
//        return paramServ;
//    }
//
//    public void setParamServ(ParameterService paramServ) {
//        this.paramServ = paramServ;
//    }
//
//    @Override
//    public JWSAlgorithm getAlgorithm() {
//        if (!org.springframework.util.StringUtils.isEmpty(paramServ.getParam("ASYNC_SIGNATURE")) && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
//            return JWSAlgorithm.RS256;
//        }
//        return JWSAlgorithm.HS256;
//    }
//
//}
//
//import java.io.UnsupportedEncodingException;
//import java.security.Key;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//
//import com.nimbusds.jose.JWSAlgorithm;
//
//public class KeyStoreServiceImp implements KeyStoreService {
//
//	@Override
//	public Key getHttpSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,
//			UnsupportedEncodingException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Key getHttpSigPublicKey() throws KeyStoreException, UnsupportedEncodingException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public JWSAlgorithm getAlgorithm() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int getNumAttempts() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//}
