package eu.atos.seal.rm.service.cm;

import java.util.List;

import eu.atos.seal.rm.model.AttributeTypeList;
import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.service.rm.model.MsMetadataList;

public interface ConfMngrConnService
{
	public List<String> getAttributeProfiles ();
	public AttributeTypeList getAttributeSetByProfile(String profileId);
	//getMappingList (String profileId);
	
	public List<String> getExternalEntities (); // returns available **collections**
	public EntityMetadataList getEntityMetadataSet (String collectionId);
	public EntityMetadata getEntityMetadata (String collectionId, String entityId);
	
	public MsMetadataList getAllMicroservices ();
	public MsMetadataList getMicroservicesByApiClass (String apiClasses); // input like "SP, IDP, AP, GW, ACM, SM, CM"
	
	public List<String> getInternalConfs (); // returns available internal configurations
	public EntityMetadata getConfiguration (String confId);
}
