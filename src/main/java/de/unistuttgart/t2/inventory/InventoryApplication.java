package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import de.unistuttgart.t2.inventory.config.ExculdeSagaConfig;
import de.unistuttgart.t2.inventory.config.IncludeSagaConfig;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.TimeoutCollector;

@Import({IncludeSagaConfig.class, ExculdeSagaConfig.class})
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class}) // or else we wont find the mongo bean
@SpringBootApplication
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
	
	@Bean
	public InventoryService inventoryService() {
		return new InventoryService();
	}

}
