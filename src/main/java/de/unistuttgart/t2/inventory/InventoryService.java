package de.unistuttgart.t2.inventory;

import java.util.List;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

/**
 * 
 * Interactions with the product repository that involve reservations.
 * 
 * Lol, i figured out how to use that damn transaction manager. thus,
 * 'transactional' annotation works again.
 * 
 * @author maumau
 *
 */
public class InventoryService {

    @Autowired
    ProductRepository productRepository;

    /**
     * commit reservations associated with given sessionId.
     * 
     * @param sessionId to identify the reservations to delete
     */
    @Transactional
    public void handleSagaAction(String sessionId) {
        List<InventoryItem> items = productRepository.findAll();
        for (InventoryItem item : items) {
            item.commitReservation(sessionId);
        }
        productRepository.saveAll(items);
    }

    /**
     * delete reservations of cancelled order from repository.
     * 
     * @param sessionId to identify which reservations to delete
     */
    @Transactional
    public void handleSagaCompensation(String sessionId) {
        List<InventoryItem> items = productRepository.findAll();
        for (InventoryItem item : items) {
            item.getReservations().remove(sessionId);
        }
        productRepository.saveAll(items);
    }

    /**
     * attach a reservation for the given session to the given product.
     * 
     * @param productId products to reserve of
     * @param sessionId user to reserve for
     * @param units     amount to reserve
     * @throws NoSuchElementException   if the product does not exist
     * @throws IllegalArgumentException if any parameter is null
     */
    @Transactional
    public InventoryItem makeReservation(String productId, String sessionId, int units) throws NoSuchElementException {
        if (productId == null || sessionId == null || units < 0) {
            throw new IllegalArgumentException(
                    "productId : " + productId + ", sessionId : " + sessionId + ", units : " + units);
        }
        InventoryItem item = productRepository.findById(productId).orElseThrow(
                () -> new NoSuchElementException(String.format("product with id %s not found", productId)));

        item.addReservation(sessionId, units);
        return productRepository.save(item);
    }
}