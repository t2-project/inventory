package de.unistuttgart.t2.inventory;

import java.util.List;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

/**
 * 
 * Interactions with the product repository that involve reservations.
 * 
 * On Transactions: The 'Transactional' annotation (apparently) does not work
 * with MongoDB. There is a MongoTransactionManager but i could not figure out
 * how to make it work with the Spring RepositoryTemplate. Thus, good old
 * semaphores it is. Might not be optimal but at least it works :)
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
    public void handleSagaAction(String sessionId) {
        synchronized (this) {
            List<InventoryItem> items = productRepository.findAll();
            for (InventoryItem item : items) {
                item.commitReservation(sessionId);
            }
            productRepository.saveAll(items);
        }
    }

    /**
     * delete reservations of cancelled order from repository.
     * 
     * @param sessionId to identify which reservations to delete
     */
    public void handleSagaCompensation(String sessionId) {
        synchronized (this) {
            List<InventoryItem> items = productRepository.findAll();
            for (InventoryItem item : items) {
                item.getReservations().remove(sessionId);
            }
            productRepository.saveAll(items);
        }
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
    public InventoryItem makeReservation(String productId, String sessionId, int units) throws NoSuchElementException {
        if (productId == null || sessionId == null || units < 0) {
            throw new IllegalArgumentException(
                    "productId : " + productId + ", sessionId : " + sessionId + ", units : " + units);
        }
        synchronized (this) {
            InventoryItem item = productRepository.findById(productId).orElseThrow(
                    () -> new NoSuchElementException(String.format("product with id %s not found", productId)));

            item.addReservation(sessionId, units);
            return productRepository.save(item);
        }
    }
}