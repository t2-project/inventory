package de.unistuttgart.t2.inventory;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.unistuttgart.t2.inventory.domain.Product;
import de.unistuttgart.t2.inventory.repository.ProductRepository;

/**
 * Service responsible for the inventory.
 * 
 * @author maumau
 *
 */
public class InventoryService {

	// TODO auto wired inventory repo
	@Autowired
	ProductRepository productRepository;
	
	/**
	 * delete reservation with given id.
	 * 
	 * they are no sold and need not be saved any longer. 
	 * this is retry-able. if i fail while deleting, i try again. deleting is idempotent. 
	 * 
	 * @param id the reservation's id.
	 */
	@Transactional
	public void commitReservation(String id) {
		// TODO : lock the repository
		List<Product> products = productRepository.findAll();
		for (Product product : products) {
			product.commitReservation(id);
			productRepository.save(product);
		}
		// TODO :  unlock repository	
	}
	
	/**
	 * undo the reservation.
	 * 
	 * remove the reservations from the db and increase the stock pile of the products
	 * 
	 * @param id
	 */
	@Transactional
	public void undoReservation(String id) {
		// TODO : lock the repository
		List<Product> products = productRepository.findAll();
		for (Product product : products) {
			product.undoReservation(id);
			productRepository.save(product);
		}
		// TODO :  unlock repository
	}
	
	public Product addProduct(Product p) {
		return productRepository.save(p);
	}
}
