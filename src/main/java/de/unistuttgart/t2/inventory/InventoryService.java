package de.unistuttgart.t2.inventory;

import java.util.List;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

/**
 * Service responsible for the inventory.
 * 
 * @author maumau
 *
 */
@Transactional(isolation = Isolation.SERIALIZABLE)
public class InventoryService {

	@Autowired
	ProductRepository productRepository;

	/**
	 * commit reservations
	 * 
	 * they are no sold and need not be saved any longer. this is retry-able. if i
	 * fail while deleting, i try again. deleting is idempotent.
	 * 
	 * @param id the reservation's id.
	 */
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
	 * 
	 * @param sessionId - to identify which reservations to delete
	 */
	public void handleSagaCompensation(String sessionId) {
		List<InventoryItem> items = productRepository.findAll();
		for (InventoryItem item : items) {
			item.getReservations().remove(sessionId);
		}
		productRepository.saveAll(items);
	}

	/**
	 * adds a reservation to a product.
	 * 
	 * each time a user wants to add some units of a product to their cart, a
	 * reservation is also added to the product. This ensures that the saga cannot
	 * fail at the inventory due to not enough units of a product in stock.
	 * 
	 * TODO: there is only one reservation slot per product per user. if a
	 * user adds products, commits his order (the saga gets started) and adds (or
	 * deletes) new products they will seen as part of the saga, eventhough they are not.
	 * solutions to this problem would be
	 * 	 * a) to timestamp each reservation (usefull, as i want to timestamp them later on for garbage collection)
	 *   * b) save reservations by orderId (would require to generate the order id before the saga is started)
	 *   * c) reset the session after saga start, such that reservations cannot change anymore.
	 * 
	 * 
	 * @param productId - products to reserve of
	 * @param sessionId - user to reserve for
	 * @param units     - amount to reserve
	 * @throws NoSuchElementException   if the product does not exist
	 * @throws IllegalArgumentException caused by addReservation
	 */
	public InventoryItem makeReservation(String productId, String sessionId, int units) throws NoSuchElementException {
		if (productId == null || sessionId == null || units < 0) {
			throw new IllegalArgumentException("productId : " + productId + ", sessionId : " + sessionId + ", units : " + units);
		}
		InventoryItem item = productRepository.findById(productId).orElseThrow(() -> new NoSuchElementException());

		item.addReservation(sessionId, units);
		return productRepository.save(item);

	}
}