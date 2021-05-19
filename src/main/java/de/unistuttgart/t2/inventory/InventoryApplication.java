package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import de.unistuttgart.t2.inventory.config.ExculdeSagaConfig;
import de.unistuttgart.t2.inventory.config.IncludeSagaConfig;

@Import({IncludeSagaConfig.class, ExculdeSagaConfig.class})
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
