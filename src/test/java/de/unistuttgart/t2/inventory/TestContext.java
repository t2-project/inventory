package de.unistuttgart.t2.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.repository.ProductRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class})
@Profile("unittest")
public class TestContext  {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
    
    @Bean
    public InventoryService inventoryService() {
        return new InventoryService();
    }
}
