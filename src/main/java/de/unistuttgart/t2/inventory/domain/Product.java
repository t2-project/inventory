package de.unistuttgart.t2.inventory.domain;

import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * A Product of the Store.
 * 
 * @author maumau
 *
 */
public class Product {
	@Id
	private String id;
	private String name;
	private String description;

	// number units of this product.
	// the 'true' number of products is amount + all reservations.
	private int amount;

	private double price;

	private Map<String, Integer> reservations;

	
	public Product() {
	}

	public Product(String id, String name, String description, int amount, double price, Map<String, Integer> reservations) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.amount = amount;
		this.price = price;
		this.reservations = reservations;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public Map<String, Integer> getReservations() {
		return reservations;
	}

	public void setReservations(Map<String, Integer> reservations) {
		this.reservations = reservations;
	}

	@Override
	public String toString() {
		return this.id + ", " + this.name + ", " + this.description + ", " + this.amount + ", " + this.price;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Product)) {
			return false;
		}

		return this.id.equals(((Product) o).id) && this.name.equals(((Product) o).name) && this.description.equals(((Product) o).description)
				&& this.amount == ((Product) o).amount && this.price == ((Product) o).price && this.reservations.equals(((Product) o).reservations);
	}
	
	/**
	 * 
	 * @param reservationId
	 */
	public void undoReservation(String reservationId) {
		int numberOfReservedItems =  reservations.get(reservationId);
		this.amount += numberOfReservedItems;
		reservations.remove(reservationId);
	}
	
	/**
	 * 
	 * @param reservationId
	 */
	public void commitReservation(String reservationId) {
		reservations.remove(reservationId);
	}
}
