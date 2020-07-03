package eu.atos.seal.rm.service.cm;

import java.util.ArrayList;
import java.util.List;

import eu.atos.seal.rm.model.EntityMetadata;
import eu.atos.seal.rm.model.EntityMetadataList;
import eu.atos.seal.rm.model.EntityMetadataPairList;
//import javafx.util.Pair;

public interface ApigwclController {

	// /metadata/externalEntities
	//https://vm.project-seal.eu:9053
	// /cl/list/{collection}
	EntityMetadataPairList getCollectionList(String collectionName);

}