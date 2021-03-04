package de.unistuttgart.t2.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.unistuttgart.t2.inventory.domain.Product;

public interface ProductRepository extends MongoRepository<Product, String> { 

}
