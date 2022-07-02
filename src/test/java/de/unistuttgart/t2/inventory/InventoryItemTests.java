package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.Reservation;

public class InventoryItemTests {

    private InventoryItem item;
    String session1 = "session1";
    String session2 = "session2";
    String session3 = "session3";

    @BeforeEach
    void setUp() {
        item = new InventoryItem("id", "name", "description", 15, 0.5);
        item.addReservation(session1, 1);
        item.addReservation(session2, 2);
        item.addReservation(session3, 3);

    }

    @DisplayName("testReservationNeverNull")
    @Test
    public void testReservationNeverNull() {
        assertNotNull((new InventoryItem()).getReservations());
        assertNotNull((new InventoryItem("", "", "", 0, 0)).getReservations());
    }

    @Test
    public void testAvailableUnits_correctValue() {
        assertEquals(9, item.getAvailableUnits());
    }

    @Test
    public void testAvailableUnits_exception() {
        item = new InventoryItem("id", "name", "description", 0, 0.5, List.of(new Reservation(3, "foo", item)));

        Assertions.assertThrows(IllegalStateException.class, () -> {
            item.getAvailableUnits();
        });
    }

    @Test
    public void testAddReservation() {
        item.addReservation("session4", 2);
        assertEquals(4, item.getReservations().size());
        assertEquals(7, item.getAvailableUnits());
    }

    @Test
    public void testUpdateReservation() {
        item.addReservation("session3", 2);
        assertEquals(3, item.getReservations().size());
        assertEquals(7, item.getAvailableUnits());
    }

    @Test
    public void testAddReservation_stateUnchanged() {
        item.addReservation("session4", 0);
        assertEquals(3, item.getReservations().size());
        assertEquals(9, item.getAvailableUnits());
    }

    @Test
    public void testAddReservation_exceptionTooMuchUnits_stateUnchanged() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            item.addReservation("session4", 400);
        });
        assertEquals(3, item.getReservations().size());
        assertEquals(9, item.getAvailableUnits());
    }

    @Test
    public void testAddReservation_exceptionNegativeUnits_stateUnchanged() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            item.addReservation("session4", -4);
        });
        assertEquals(3, item.getReservations().size());
        assertEquals(9, item.getAvailableUnits());
    }

    @Test
    public void testCommitReservation_fair() {
        item.commitReservation("session1");
        assertEquals(2, item.getReservations().size());
        assertEquals(9, item.getAvailableUnits()); // unchanged
        assertEquals(14, item.getUnits()); // changed
    }

    @Test
    public void testCommitReservation_missingId() {
        item.commitReservation("sessionX");
        assertEquals(3, item.getReservations().size());
        assertEquals(9, item.getAvailableUnits()); // unchanged
        assertEquals(15, item.getUnits()); // unchanged
    }

    @Test
    public void testEquals() {
        assertTrue(item.equals(item));

        InventoryItem other = new InventoryItem("id", "name", "description", 15, 0.5);
        other.addReservation(session1, 1);
        other.addReservation(session2, 2);
        other.addReservation(session3, 3);

        assertTrue(item.equals(other));
        assertTrue(other.equals(item));

    }
}
