

package eu.atos.seal.rm.model;


public class NewUpdateDataRequest {

    private String sessionId;
    private String type;
    private String data;
    private String id;
	public void setId(String objectId) {
		this.id = objectId;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getData() {
		return data;
	}
	
	public String getSessionId() {
		return sessionId;
	}

}