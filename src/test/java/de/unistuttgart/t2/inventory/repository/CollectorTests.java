package de.unistuttgart.t2.inventory.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.util.Date;
import java.util.List;

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
 * Test the correct collection of reservations that exceede their TTL.
 * 
 * @author maumau
 *
 */
class CollectorTests {

    TimeoutCollector collector;

    @Autowired
    ReservationRepository repository;
    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    public void populateRepository() {
        repository.deleteAll();

        InventoryItem item = new InventoryItem(null, "name", "desc", 100, 1.0);
        item.addReservation("sessionId", 5);
        productRepository.save(item);

        collector = new TimeoutCollector(0, 0, new ThreadPoolTaskScheduler(), repository, productRepository);
    }

    @Test
    public void collectAllEntriesTest() throws InterruptedException {

        for (int i = 0; i < 10; i++) {

            List<Reservation> reservations = repository.findAll();
            assertEquals(1, reservations.size());
            if (reservations.get(0).getCreationDate().before(Date.from(Instant.now()))) {
                // reservation now 'collectable'
                collector.new RecervationCheckAndDeleteTask().run();

                List<InventoryItem> items = productRepository.findAll();
                List<Reservation> reserve = repository.findAll();
                assertEquals(1, items.size());
                assertTrue(reserve.isEmpty());
                assertTrue(items.get(0).getReservations().isEmpty());
                
                return;
            }
            Thread.sleep(1000);
        }
        
        fail("reservation is still not deletable");
    }
}
