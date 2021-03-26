package de.unistuttgart.t2.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.unistuttgart.t2.inventory.domain.InventoryItem;

public interface ProductRepository extends MongoRepository<InventoryItem, String> { 

}
