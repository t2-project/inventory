package de.unistuttgart.t2.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.t2.common.Product;
import de.unistuttgart.t2.common.ReservationRequest;
import de.unistuttgart.t2.inventory.repository.DataGenerator;
import de.unistuttgart.t2.inventory.repository.InventoryItem;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 
 * Defines additional endpoints for the inventory.
 * 
 * Other endpoints are auto generated.
 * 
 * @author maumau
 *
 */
@RestController
public class InventoryController {

    private InventoryService inventoryService;
    private DataGenerator generator;

    public InventoryController(@Autowired InventoryService inventoryService, @Autowired DataGenerator generator) {
        assert (generator != null && inventoryService != null);
        this.inventoryService = inventoryService;
        this.generator = generator;
    }

    /**
     * add a reservation to a product.
     * 
     * @param body request body
     * @return the product that the reservation was added to.
     */
    @Operation(summary = "Place a reservation", description = "Place a reservation of a number of units for a certain item for a certain user.")

    @PostMapping("/inventory/reservation")
    public Product addReservation(@RequestBody ReservationRequest body) {
        InventoryItem item = inventoryService.makeReservation(body.getProductId(), body.getSessionId(),
                body.getUnits());
        return new Product(item.getId(), item.getName(), item.getDescription(), item.getAvailableUnits(),
                item.getPrice());
    }

    /**
     * trigger generation of new products
     * 
     * TODO post x generation request seems more reasonable
     */
    @Operation(summary = "Populate the store with new products", description = "Populate the store with new products")
    @GetMapping("/generate")
    public void generateData() {
        generator.generateProducts();
    }

    /**
     * trigger restock of all products
     * 
     * TODO post x restock request seems more reasonable
     */
    @Operation(summary = "Restock units of the store's products", description = "Restock units of the store's products")
    @GetMapping("/restock")
    public void restock() {
        generator.restockProducts();
    }
}
