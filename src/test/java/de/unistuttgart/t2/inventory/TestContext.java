package de.unistuttgart.t2.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.repository.ProductRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class})
public class TestContext {
	@Bean
	InventoryService inventoryService() {
		return new InventoryService();
	}
	
}
