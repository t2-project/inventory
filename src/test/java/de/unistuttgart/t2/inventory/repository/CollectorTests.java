package de.unistuttgart.t2.inventory.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestContext.class)
@ActiveProfiles("test")
/**
 * actually i was trying to test my collector.. but timing is a bad bitch and it
 * fucks me up.
 * 
 * @author maumau
 *
 */
class CollectorTests {

    @Autowired
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
    }

    @Test
    public void collectAllEntriesTest() throws InterruptedException {
        
        // WFT o_O
        TimeoutCollector.RecervationCheckAndDeleteTask task = collector.new RecervationCheckAndDeleteTask();

        task.run();

        List<InventoryItem> items = repository.findAll();
        
        assertFalse(items.isEmpty());
        
        for (InventoryItem item : items) {
            assertTrue(item.getReservations().isEmpty());
        }
    }
    
    @Test
    public void collectSomeEntriesTest() throws InterruptedException {
        InventoryItem otherItem = new InventoryItem("otherid", "name", "desc", 100, 1.0);
        otherItem.addReservation("otherId", 5);
        otherItem.getReservations().get("otherId").setCreationDate(Date.from(Instant.now().plusSeconds(6000)));
        
        repository.save(otherItem);
        
        // WFT o_O
        TimeoutCollector.RecervationCheckAndDeleteTask task = collector.new RecervationCheckAndDeleteTask();

        task.run();
        
        List<InventoryItem> items = repository.findAll();

        for (InventoryItem item : items) {
            if (item.getId().equals("otherid")) {
                assertFalse(item.getReservations().isEmpty());
                return;
            }
        }

        assertTrue("the item to test for went missing.", false);
    }
}
