package de.unistuttgart.t2.inventory.config;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import de.unistuttgart.t2.inventory.repository.TimeoutCollector;

/**
 * Configuration to have all beans required for the {@link TimeoutCollector TimeoutCollector}.
 *
 * @author maumau
 */
@Configuration
public class TimeoutCollectorConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
