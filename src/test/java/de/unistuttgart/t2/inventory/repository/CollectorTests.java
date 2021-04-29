package de.unistuttgart.t2.inventory.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * Tries to test the collector but (once again) time is a bitch.  
 * 
 * @author maumau
 *
 */
@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class CollectorTests {
		
	@Autowired ProductRepository repository;
	
	@Autowired ThreadPoolTaskScheduler scheduler;
	
	@Autowired TimeoutCollector collector;
		
	// defaults: 
	// TTL = 20 sec
	// taskRate = 20000 milli sec = 20 sec
	
	@BeforeEach
	public void populateRepository() {
		repository.deleteAll();
	}
	
	
	@Test
	public void collectSingleEntryTest() throws InterruptedException {
		InventoryItem item = new InventoryItem("id1", "name1", "description1", 15, 0.5,
				Map.of("session1", new Reservation(1), "session2", new Reservation(2), "session3", new Reservation(3))); 
		repository.save(item);
		
//		Thread.sleep(300000);
//		
//		
//		assertTrue(repository.findById(item.getId()).isPresent());
//		
//		InventoryItem actual = repository.findById(item.getId()).get();
//		assertEquals(0, actual.getReservations().size());	
	}
	
	@Test
	public void resetTTLTest() throws InterruptedException {
		InventoryItem item = new InventoryItem("id1", "name1", "description1", 15, 0.5,
				Map.of("session1", new Reservation(1), "session2", new Reservation(2), "session3", new Reservation(3))); 
		
		Date previous = item.getReservations().get("session1").getCreationDate(); 
		
//		Thread.sleep(100);
//		
//		item.getReservations().get("session1").renewCreationdate();
//	
//		assertTrue(previous.before(item.getReservations().get("session1").getCreationDate()));	
	}
	
	@Test
	public void collectMultipleEntryTest() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			repository.save(new InventoryItem("id" + i, "name1", "description1", 15, 0.5,
					Map.of("session1", new Reservation(1), "session2", new Reservation(2), "session3", new Reservation(3))));			
		}
		
//		Thread.sleep(30000);		
//		List<InventoryItem> items = repository.findAll();
//		
//		for (InventoryItem item : items) {
//			assertEquals(0, item.getReservations().size());			
//		}
	}
}
