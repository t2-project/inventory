package de.unistuttgart.t2.inventory;

/**
 * Service responsible for the inventory.
 * 
 * @author maumau
 *
 */
public class InventoryService {

	// TODO auto wired inventory repo

	
	/**
	 * decrease number of units of given product
	 * 
	 * fails if not enough unit available
	 * 
	 * @param productId id of sold product
	 * @param amount number of sold unit
	 */
	public void decrease(String productId, int amount) {
		//TODO
	}

	/**
	 * increase number of unit of given product.
	 * 
	 * can not fail. 
	 * 
	 * @param productId id of product
	 * @param amount number of returned units
	 */
	public void increase(String productId, int amount) {
		//TODO
	}
}
