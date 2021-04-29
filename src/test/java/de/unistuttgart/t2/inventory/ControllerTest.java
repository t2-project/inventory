package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.unistuttgart.t2.common.Product;
import de.unistuttgart.t2.common.ReservationRequest;
import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
/**
 * Test reservation endpoint
 * 
 * @author maumau
 *
 */
public class ControllerTest {
	
	@Autowired
	InventoryService inventoryService;
	
	@Autowired
	ProductRepository productRepository;
	

	InventoryController controller;
	

	@BeforeEach
	void populateRepository() {
		InventoryItem item1 = new InventoryItem("id1", "name1", "description1", 15, 0.5,
				Map.of("session1", new Reservation(1), "session2", new Reservation(2), "session3", new Reservation(3)));
		InventoryItem item2 = new InventoryItem("id2", "name2", "description2", 200, 1.5, Map.of("session1", new Reservation(4)));
		productRepository.save(item1);
		productRepository.save(item2);
		
		controller= new InventoryController();
		controller.inventoryService = inventoryService;

	}

	@DisplayName("testMakeNewReservation")
	@Test
	public void makeNewReservationTest() {
		// make reservation
		String key = "sessionId";
		ReservationRequest request = new ReservationRequest("id1", "session5", 1);
		Product actual = controller.addReservation(request);
		
		assertNotNull(actual);
		assertEquals("id1", actual.getId());
		assertEquals("name1", actual.getName());
		assertEquals("description1", actual.getDescription());
		assertEquals(0.5, actual.getPrice());
		assertEquals(8, actual.getUnits());
	}
}

