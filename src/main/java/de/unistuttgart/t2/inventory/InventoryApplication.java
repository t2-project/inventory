package de.unistuttgart.t2.inventory;

import de.unistuttgart.t2.inventory.config.ExcludeSagaConfig;
import de.unistuttgart.t2.inventory.config.IncludeSagaConfig;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.ReservationRepository;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
    public OpenAPI customOpenAPI(@Value("${info.app.version:unknown}") String version) {
        return new OpenAPI().components(new Components()).info(new Info()
            .title("T2 Inventory service API")
            .description("API of the T2-Project's inventory service.")
            .version(version));
    }
}
