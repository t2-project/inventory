package de.unistuttgart.t2.inventory.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration;

/**
 * Configuration that excludes saga related things.<br>
 * Supposed to be used when you want to run the payment without the other components.<br>
 * Tram* classes are from the actual saga that the CDC needs.
 *
 * @author maumau
 */
@EnableAutoConfiguration(exclude = {
    TramMessagingCommonAutoConfiguration.class,
    TramConsumerJdbcAutoConfiguration.class
})
@Profile("notsaga")
@Configuration
public class ExcludeSagaConfig {

}
