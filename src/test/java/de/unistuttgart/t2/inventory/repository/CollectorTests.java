package de.unistuttgart.t2.inventory.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
/**
 * 
 * @author maumau
 *
 */
class CollectorTests {

    
    TimeoutCollector collector;
    
    @Autowired
    ProductRepository repository;

    @BeforeEach
    public void populateRepository() {
        repository.deleteAll();
        for (int i = 0; i < 5; i++) {
            InventoryItem item = new InventoryItem(null, "name", "desc", 100, 1.0);
            item.addReservation("sessionId", 5);
            repository.save(item);
        }

        collector = new TimeoutCollector(0, 0, new ThreadPoolTaskScheduler(), repository);
    }

    @Test
    public void collectAllEntriesTest() throws InterruptedException {
        
        collector.new RecervationCheckAndDeleteTask().run();

        List<InventoryItem> items = repository.findAll();
        
        assertFalse(items.isEmpty());
        
        for (InventoryItem item : items) {
            assertTrue(item.getReservations().isEmpty());
        }
    }
}
