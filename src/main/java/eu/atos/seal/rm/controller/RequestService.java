package eu.atos.seal.rm.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface RequestService
{

	public String rmRequest(String token, Model model) throws JsonParseException, JsonMappingException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException;

	public String returnNothing(String sessionId, Model model);

	public String returnFromRequestUI(String sessionId, Model model);
}
