package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.repository.ProductRepository;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class})
public class TestContext {
	@Bean
	InventoryService inventoryService() {
		return new InventoryService();
	}
	
}
