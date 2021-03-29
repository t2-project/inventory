package de.unistuttgart.t2.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.unistuttgart.t2.inventory.domain.InventoryItem;

@RepositoryRestResource(
	    path = "inventory",
	    itemResourceRel = "inventory",
	    collectionResourceRel = "inventory"
	)
public interface ProductRepository extends MongoRepository<InventoryItem, String> { 

}
