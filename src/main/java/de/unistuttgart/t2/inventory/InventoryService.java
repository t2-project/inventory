package de.unistuttgart.t2.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.unistuttgart.t2.common.domain.CartContent;
import de.unistuttgart.t2.common.domain.Product;
import de.unistuttgart.t2.inventory.domain.InventoryItem;
import de.unistuttgart.t2.inventory.exception.ProductNotFoundException;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import javassist.NotFoundException;

/**
 * Service responsible for the inventory.
 * 
 * @author maumau
 *
 */
public class InventoryService {

	@Autowired
	ProductRepository productRepository;
	
//	@PostConstruct
//	public void init() {
//		ProductEntity pe1 = new ProductEntity(null, "pe1", "", 15, 2.00, null);
//		ProductEntity pe2 = new ProductEntity(null, "pe2", "", 35, 1.50, null);
//		ProductEntity pe3 = new ProductEntity(null, "pe3", "", 30, 4.00, null);
//		ProductEntity pe4 = new ProductEntity(null, "pe4", "", 72, 3.00, null);
//		productRepository.save(pe1);
//		productRepository.save(pe2);
//		productRepository.save(pe3);
//		productRepository.save(pe4);
//	}

	/**
	 * delete reservation with given id.
	 * 
	 * they are no sold and need not be saved any longer. this is retry-able. if i
	 * fail while deleting, i try again. deleting is idempotent.
	 * 
	 * @param id the reservation's id.
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void handleSagaAction() {
		// TODO i don't even know whether inventory is still part of the saga
	}

	/**
	 * undo the reservation.
	 * 
	 * remove the reservations from the db and increase the stock pile of the
	 * products
	 * 
	 * @param id
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void handleSagaCompensation() {
		// TODO i don't even know whether inventory is still part of the saga
	}

	// THINGS THAT ARE NOT SAGA RELATED
	
	public InventoryItem addProduct(InventoryItem p) {
		return productRepository.save(p);
	}

	public Optional<InventoryItem> getProductEntity(final String pid) {
		// no lock, 'cause only read
		return productRepository.findById(pid);
	}

	public Product getProduct(final String pid) {
		InventoryItem p = getProductEntity(pid)
				.orElseThrow(() -> new ProductNotFoundException("Product with id " + pid + " not found."));
		return new Product(p.getId(), p.getName(), p.getUnits(), p.getPrice());
	}
	
	public List<Product> getAllProducts() {
		List<Product> rval = new ArrayList<>();
		List<InventoryItem> entities = productRepository.findAll();
		for (InventoryItem p : entities) {
			rval.add(new Product(p.getId(), p.getName(), p.getUnits(), p.getPrice()));
		}
		return rval;
	}
}
