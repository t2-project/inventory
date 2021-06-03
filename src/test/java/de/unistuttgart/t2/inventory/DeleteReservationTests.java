package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DeleteReservationTests {
    
	@Autowired
	ProductRepository productRepository;
	
	String id1, id2;

	@BeforeEach
	void populateRepository() {
	    InventoryItem item1 = new InventoryItem("id1", "name1", "description1", 15, 0.5,
				Map.of("session1", new Reservation(1, "session1"), "session2", new Reservation(2,"session2"), "session3", new Reservation(3,"session3")));
		InventoryItem item2 = new InventoryItem("id2", "name2", "description2", 200, 1.5, Map.of("session1", new Reservation(4,"session1")));
		id1 = productRepository.save(item1).getId();
		id2 = productRepository.save(item2).getId();
	}

	@Test
	public void handleSagaActionTest(@Autowired InventoryService inventoryService) {
		// make reservation
		String key = "session1";
		inventoryService.handleSagaAction(key);

		// assert things
		assertEquals(2, productRepository.count());

		// assert item id1
		InventoryItem actual = productRepository.findById(id1).get();
		
	    assertEquals(14, actual.getUnits());
	    assertEquals(9, actual.getAvailableUnits());
	        
		
		Map<String, Reservation> actualReservation = actual.getReservations();

		assertEquals(2, actualReservation.size());
		assertFalse(actualReservation.containsKey(key));
		
		// assert item id2
		actual = productRepository.findById(id2).get();
		
		assertEquals(196, actual.getUnits());
		assertEquals(196, actual.getAvailableUnits());
		
		actualReservation = actual.getReservations();
		
		assertEquals(0, actualReservation.size());
        assertFalse(actualReservation.containsKey(key));	
	}

	    @Test
	    public void handleSagaCompensationTest(@Autowired InventoryService inventoryService) {
	        // make reservation
	        String key = "session1";
	        inventoryService.handleSagaCompensation(key);

	        // assert things
	        assertEquals(2, productRepository.count());

	        // assert item id1
	        InventoryItem actual = productRepository.findById(id1).get();
	        
	        assertEquals(15, actual.getUnits());
	        assertEquals(10, actual.getAvailableUnits());
	            
	        
	        Map<String, Reservation> actualReservation = actual.getReservations();

	        assertEquals(2, actualReservation.size());
	        assertFalse(actualReservation.containsKey(key));
	        
	        // assert item id2
	        actual = productRepository.findById(id2).get();
	        
	        assertEquals(200, actual.getUnits());
	        assertEquals(200, actual.getAvailableUnits());
	        
	        actualReservation = actual.getReservations();
	        
	        assertEquals(0, actualReservation.size());
	        assertFalse(actualReservation.containsKey(key));    
	    }
}

