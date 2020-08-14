package eu.atos.seal.rm.service.sm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;


public interface SessionManagerConnService
{
	public String startSession() throws UnrecoverableKeyException, KeyStoreException, 
										FileNotFoundException, NoSuchAlgorithmException, 
										CertificateException, InvalidKeySpecException, IOException;
	
	public String generateToken(String sessionId, String receiver) throws UnrecoverableKeyException, KeyStoreException, 
	 													 FileNotFoundException, NoSuchAlgorithmException, 
	 													 CertificateException, InvalidKeySpecException, IOException ;
	
	public String validateToken(String token) throws UnrecoverableKeyException, KeyStoreException, 
													 FileNotFoundException, NoSuchAlgorithmException, 
													 CertificateException, InvalidKeySpecException, IOException ;
	
	public Object readVariable( String sessionId, String variableName) throws UnrecoverableKeyException, KeyStoreException, 
																			  FileNotFoundException, NoSuchAlgorithmException, 
																			  CertificateException, InvalidKeySpecException, IOException ;
	// Returns the list of "dataSet"/"linkRequest" objects from the DataStore.
	// If type is null, returns the complete DataStore.
	public Object readDS( String sessionId, String type) throws UnrecoverableKeyException, KeyStoreException, 
	  															FileNotFoundException, NoSuchAlgorithmException, 
	  															CertificateException, InvalidKeySpecException, IOException ;


	public void deleteSession(String sessionId) throws UnrecoverableKeyException, KeyStoreException, 
													   FileNotFoundException, NoSuchAlgorithmException, 
													   CertificateException, InvalidKeySpecException, IOException;  
	
	public String getSession(String varName, String varValue) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException;

	public HashMap<String, Object> readVariables(String sessionId) throws UnrecoverableKeyException, KeyStoreException,
																		  FileNotFoundException, NoSuchAlgorithmException, 
																		  CertificateException, InvalidKeySpecException, IOException;

	public void updateVariable(String sessionId, String varName, String varValue)throws UnrecoverableKeyException, KeyStoreException,
	  																  FileNotFoundException, NoSuchAlgorithmException, 
	  																  CertificateException, InvalidKeySpecException, IOException;

	
}
