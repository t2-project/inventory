package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.unistuttgart.t2.inventory.config.*;
import de.unistuttgart.t2.inventory.repository.*;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;

@Import({ IncludeSagaConfig.class, ExcludeSagaConfig.class })
@EnableJpaRepositories(basePackageClasses = { ProductRepository.class, ReservationRepository.class })
@EnableTransactionManagement
@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    public InventoryService inventoryService(ProductRepository repository,
        ReservationRepository reservationRepository) {
        return new InventoryService(repository, reservationRepository);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components())
            .info(new Info().title("Inventory service API")
                .description("API of the T2-Project's inventory service."));
    }
}
