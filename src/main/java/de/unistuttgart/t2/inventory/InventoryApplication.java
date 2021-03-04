package de.unistuttgart.t2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.saga.InventoryCommandHandler;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;

@Import({SagaParticipantConfiguration.class,
	TramMessageProducerJdbcConfiguration.class,
    EventuateTramKafkaMessageConsumerConfiguration.class})//, OptimisticLockingDecoratorConfiguration.class})
@EnableJpaRepositories
@EnableAutoConfiguration
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {ProductRepository.class}) // or else we wont find the repo beans
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

	@Bean
	public InventoryService inventoryService() {
		return new InventoryService();
	}

	
	//create repository beans yes/no? i guess no :x
	
	@Bean
	public InventoryCommandHandler inventoryCommandHandler() {
		return new InventoryCommandHandler();
	}

	@Bean
	public SagaCommandDispatcher inventoryCommandDispatcher(InventoryCommandHandler target,
			SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

		return sagaCommandDispatcherFactory.make("inventoryCommandDispatcher", target.commandHandlers());
	}

}
