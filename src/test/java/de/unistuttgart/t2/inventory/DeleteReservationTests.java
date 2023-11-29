package de.unistuttgart.t2.inventory;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DeleteReservationTests extends RepositoryTests {

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

        List<Reservation> actualReservation = actual.getReservations();

        assertEquals(2, actualReservation.size());
        assertReservationAbsence(actualReservation, key);

        // assert item id2
        actual = productRepository.findById(id2).get();

        assertEquals(196, actual.getUnits());
        assertEquals(196, actual.getAvailableUnits());

        actualReservation = actual.getReservations();

        assertEquals(0, actualReservation.size());
        assertReservationAbsence(actualReservation, key);
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

        List<Reservation> actualReservation = actual.getReservations();

        assertEquals(2, actualReservation.size());
        assertReservationAbsence(actualReservation, key);

        // assert item id2
        actual = productRepository.findById(id2).get();

        assertEquals(200, actual.getUnits());
        assertEquals(200, actual.getAvailableUnits());

        actualReservation = actual.getReservations();

        assertEquals(0, actualReservation.size());
        assertReservationAbsence(actualReservation, key);
    }
}
