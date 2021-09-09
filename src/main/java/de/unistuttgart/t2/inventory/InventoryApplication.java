package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.unistuttgart.t2.inventory.config.ExculdeSagaConfig;
import de.unistuttgart.t2.inventory.config.IncludeSagaConfig;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.ReservationRepository;

@Import({IncludeSagaConfig.class, ExculdeSagaConfig.class})
@EnableJpaRepositories(basePackageClasses = {ProductRepository.class, ReservationRepository.class})
@EnableTransactionManagement
@SpringBootApplication
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
	
	@Bean
	public InventoryService inventoryService(ProductRepository repository, ReservationRepository reservationRepository) {
		return new InventoryService(repository, reservationRepository);
	}

}
