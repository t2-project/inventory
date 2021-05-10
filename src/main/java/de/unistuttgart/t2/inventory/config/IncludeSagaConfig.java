package de.unistuttgart.t2.inventory.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.unistuttgart.t2.inventory.saga.InventoryCommandHandler;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;

/**
 * Configuration to run application with saga. 
 * 
 * Supposed to be used when CDC service is up and running somewhere.
 * 
 * @author maumau
 *
 */
@Import({ SagaParticipantConfiguration.class, TramMessageProducerJdbcConfiguration.class,
		EventuateTramKafkaMessageConsumerConfiguration.class, OptimisticLockingDecoratorConfiguration.class})
@EnableJpaRepositories
@EnableAutoConfiguration
@Profile("saga")
@Configuration
public class IncludeSagaConfig {

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
