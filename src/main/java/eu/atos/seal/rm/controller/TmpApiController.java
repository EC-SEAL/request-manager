package eu.atos.seal.rm.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TmpApiController
{
	private static final Logger log = LoggerFactory.getLogger(TmpApiController.class);
	
	
	@Autowired
	private RequestService requestService;
	
	@Autowired
	private HttpSession session;
	
	
//	@GetMapping("/tmp/urlReturn")
//	String tmpUrlReturn(String sessionId,Model model) throws Exception
//	{
//	
//	}
	
	@GetMapping("/urlFinishProcess")
	public String accept (String sessionId,Model model, String requestSource,String pdsRequestSelection) throws Exception
	{
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@2222ACCEPT: Entering urlFinishProcess ...");
		log.info("sessionID:"+sessionId);
		log.info("requestSource:"+requestSource);
		log.info("pdsSource:"+pdsRequestSelection);
		
		System.out.println("sesion"+session.getAttribute("sessionId"));
		sessionId = (String) session.getAttribute("sessionId");
		requestSource =(String) session.getAttribute("requestSource");
		pdsRequestSelection =(String) session.getAttribute("pdsSource");
		log.info("2.sessionID:"+sessionId);
		log.info("2.requestSource:"+requestSource);
		log.info("2.pdsSource:"+pdsRequestSelection);
		//return responseService.returnFromResponseUI (sessionId, model);
		//List<String> attrRequestList = new List<String>();
		return requestService.returnFromUI(sessionId, model, requestSource, pdsRequestSelection, null);
	}
	
//	@PostMapping("/urlFinishProcess")
//	public String metodoPost(Model model)
//	{
//		log.info("En metodoPost");
//		log.info("model: "+model.toString());
//		log.info("session: "+ session.getAttribute("UrlToRedirect"));
//		model.addAttribute("\"UrlToRedirect\"",  session.getAttribute("UrlToRedirect"));
//		model.addAttribute("msToken", session.getAttribute("msToken"));
//		log.info("model: "+model.toString());
//		return "/redirectform";
//	}
	
	


}
