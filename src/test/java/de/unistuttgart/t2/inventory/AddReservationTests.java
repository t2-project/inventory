package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import de.unistuttgart.t2.inventory.InventoryService;
import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AddReservationTests {

	@Autowired
	ProductRepository productRepository;

	@BeforeEach
	void populateRepository() {
		InventoryItem item1 = new InventoryItem("id1", "name1", "description1", 15, 0.5,
				Map.of("session1", new Reservation(1), "session2", new Reservation(2), "session3", new Reservation(3)));
		InventoryItem item2 = new InventoryItem("id2", "name2", "description2", 200, 1.5, Map.of("session1", new Reservation(4)));
		productRepository.save(item1);
		productRepository.save(item2);
	}

	@DisplayName("testMakeNewReservation")
	@Test
	public void makeNewReservationTest(@Autowired InventoryService inventoryService) {
		// make reservation
		String key = "sessionId";
		inventoryService.makeReservation("id1", key, 1);

		// assert things
		assertEquals(2, productRepository.count());

		Map<String, Reservation> actual = productRepository.findById("id1").get().getReservations();

		assertEquals(4, actual.size());
		assertTrue(actual.containsKey(key));
		assertEquals(1, actual.get(key).getUnits());
	}
	
	@DisplayName("testMakeNoNewReservation")
	@Test
	public void makeNoNewReservationTest(@Autowired InventoryService inventoryService) {
		// make reservation
		String key = "sessionId";
		inventoryService.makeReservation("id1", key, 0);

		// assert things
		assertEquals(2, productRepository.count());

		Map<String, Reservation> actual = productRepository.findById("id1").get().getReservations();

		assertEquals(3, actual.size());
		assertFalse(actual.containsKey(key));
	}

	@DisplayName("testIncreaseReservation")
	@Test
	public void increaseReservationTest(@Autowired InventoryService inventoryService) {
		// make reservation
		String key = "session1";
		inventoryService.makeReservation("id1", key, 1);

		// assert things
		assertEquals(2, productRepository.count());

		Map<String, Reservation> actual = productRepository.findById("id1").get().getReservations();

		assertEquals(3, actual.size());
		assertTrue(actual.containsKey(key));
		assertEquals(2, actual.get(key).getUnits());
	}

	@DisplayName("testUnchangedReservation")
	@Test
	public void unchangedReservationTest(@Autowired InventoryService inventoryService) {
		// make reservation
		String key = "session1";
		inventoryService.makeReservation("id1", key, 0);

		// assert things
		assertEquals(2, productRepository.count());

		Map<String, Reservation> actual = productRepository.findById("id1").get().getReservations();

		assertEquals(3, actual.size());
		assertTrue(actual.containsKey(key));
		assertEquals(1, actual.get(key).getUnits());
	}
	
	@DisplayName("testIAEProductId")
	@Test
	public void throwIAEProductIDReservationTest(@Autowired InventoryService inventoryService) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			inventoryService.makeReservation(null, "session1", 1);
		});
	}
	
	@DisplayName("testIAESessionId")
	@Test
	public void throwIAESessionIDReservationTest(@Autowired InventoryService inventoryService) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			inventoryService.makeReservation("id1", null, 1);
		});
	}
	
	@DisplayName("testIAENegativeUnits")
	@Test
	public void throwIAENegativeUnitsReservationTest(@Autowired InventoryService inventoryService) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			inventoryService.makeReservation("id1", "session1", -1);
		});
	}
	
	@DisplayName("testIAENotEnoughUnitsinStock")
	@Test
	public void throwIAEUnitsReservationTest(@Autowired InventoryService inventoryService) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			inventoryService.makeReservation("id1", "session1", 1000);
		});
	}

	@DisplayName("testNSEE")
	@Test
	public void throwNSEEReservationTest(@Autowired InventoryService inventoryService) {
		Assertions.assertThrows(NoSuchElementException.class, () -> {
			inventoryService.makeReservation("wrongid", "session1", 1);
		});
	}
}
