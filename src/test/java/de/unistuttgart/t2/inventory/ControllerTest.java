package de.unistuttgart.t2.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.unistuttgart.t2.common.Product;
import de.unistuttgart.t2.common.ReservationRequest;
import de.unistuttgart.t2.inventory.repository.DataGenerator;
import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
/**
 * Test reservation endpoint
 * 
 * @author maumau
 */
public class ControllerTest extends RepositoryTests {

    @Autowired
    InventoryService inventoryService;

    InventoryController controller;

    @BeforeEach
    void prepareController() {
        controller = new InventoryController(inventoryService, new DataGenerator(productRepository, 0));
    }

    @Test
    public void makeNewReservationTest() {
        // make reservation
        ReservationRequest request = new ReservationRequest(id1, "session5", 1);
        Product actual = controller.addReservation(request);

        assertNotNull(actual);
        assertEquals(id1, actual.getId());
        assertEquals("name1", actual.getName());
        assertEquals("description1", actual.getDescription());
        assertEquals(0.5, actual.getPrice());
        assertEquals(8, actual.getUnits());
    }
}
