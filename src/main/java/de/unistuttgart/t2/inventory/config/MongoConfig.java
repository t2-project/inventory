package de.unistuttgart.t2.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.repository.ProductRepository;

/**
 * Configuration for the MongoDB.
 * Configures it, such that Transactional work. 
 * 
 * @author maumau
 *
 */
@Configuration
@Profile("saga")
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class}) // or else we wont find the mongo bean
public class MongoConfig extends AbstractMongoClientConfiguration{

    
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        return "data";
    }
}