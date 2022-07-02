package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;
import de.unistuttgart.t2.inventory.repository.ReservationRepository;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
/**
 * Test the adding of reservations.
 * 
 * @author maumau
 */
public class AddReservationTests extends RepositoryTests {

    @DisplayName("testMakeNewReservation")
    @Test
    public void makeNewReservationTest(@Autowired InventoryService inventoryService) {
        // make reservation
        String key = "sessionId";
        inventoryService.makeReservation(id1, key, 1);

        // assert things
        assertEquals(2, productRepository.count());

        List<Reservation> actuals = productRepository.findById(id1).get().getReservations();

        assertEquals(4, actuals.size());

        Reservation actual = getReservation(actuals, key);

        assertEquals(1, actual.getUnits());
    }

    @DisplayName("testMakeNoNewReservation")
    @Test
    public void makeNoNewReservationTest(@Autowired InventoryService inventoryService) {
        // make reservation
        String key = "sessionId";
        inventoryService.makeReservation(id1, key, 0);

        // assert things
        assertEquals(2, productRepository.count());

        List<Reservation> actual = productRepository.findById(id1).get().getReservations();

        assertEquals(3, actual.size());
        actual = actual.stream().filter(r -> r.getUserId().equals(key)).collect(Collectors.toList());
        assertTrue(actual.isEmpty());
    }

    @DisplayName("testIncreaseReservation")
    @Test
    public void increaseReservationTest(@Autowired InventoryService inventoryService) {
        // make reservation
        String key = "session1";
        inventoryService.makeReservation(id1, key, 1);

        // assert things
        assertEquals(2, productRepository.count());

        List<Reservation> actual = productRepository.findById(id1).get().getReservations();

        assertEquals(3, actual.size());
        actual = actual.stream().filter(r -> r.getUserId().equals(key)).collect(Collectors.toList());

        assertEquals(1, actual.size());
        assertEquals(2, actual.get(0).getUnits());
    }

    @DisplayName("testUnchangedReservation")
    @Test
    public void unchangedReservationTest(@Autowired InventoryService inventoryService) {
        // make reservation
        String key = "session1";
        inventoryService.makeReservation(id1, key, 0);

        // assert things
        assertEquals(2, productRepository.count());

        List<Reservation> actual = productRepository.findById(id1).get().getReservations();

        assertEquals(3, actual.size());
        actual = actual.stream().filter(r -> r.getUserId().equals(key)).collect(Collectors.toList());

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getUnits());
    }

    @DisplayName("testIAEProductId")
    @Test
    public void throwIAEProductIDReservationTest(@Autowired InventoryService inventoryService) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.makeReservation(null, session1, 1);
        });
    }

    @DisplayName("testIAESessionId")
    @Test
    public void throwIAESessionIDReservationTest(@Autowired InventoryService inventoryService) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.makeReservation(id1, null, 1);
        });
    }

    @DisplayName("testIAENegativeUnits")
    @Test
    public void throwIAENegativeUnitsReservationTest(@Autowired InventoryService inventoryService) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.makeReservation(id1, session1, -1);
        });
    }

    @DisplayName("testIAENotEnoughUnitsinStock")
    @Test
    public void throwIAEUnitsReservationTest(@Autowired InventoryService inventoryService) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.makeReservation(id1, session1, 1000);
        });
    }

    @DisplayName("testNSEE")
    @Test
    public void throwNSEEReservationTest(@Autowired InventoryService inventoryService) {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            inventoryService.makeReservation("wrongid", session1, 1);
        });
    }
}
