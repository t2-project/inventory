package de.unistuttgart.t2.payment.repository;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.InventoryService;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class})
public class TestContext {

	@Bean
	public InventoryService inventoryService() {
		return new InventoryService();
	}

	
}
