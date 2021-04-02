package de.unistuttgart.t2.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import de.unistuttgart.t2.inventory.repository.ProductRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class})
public class TestContext {
	@Bean
	InventoryService inventoryService() {
		return new InventoryService();
	}
}
