package de.unistuttgart.t2.inventory.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Product of the Store.
 * 
 * @author maumau
 *
 */
public class InventoryItem {
	@Id
	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;

	// number units of this product.
	// the 'true' number of products is amount + all reservations.
	@JsonProperty("units")
	private int units;

	@JsonProperty("price")
	private double price;

	// sessionid -> units
	private Map<String, Integer> reservations;

	
	public InventoryItem() {
		this.reservations = new HashMap<String, Integer>();
	}

	@JsonCreator
	public InventoryItem(String id, String name, String description, int units, double price) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.units = units;
		this.price = price;
		this.reservations = new HashMap<String, Integer>();
	}
	
	public InventoryItem(String id, String name, String description, int units, double price, Map<String, Integer> reservations) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.units = units;
		this.price = price;
		this.reservations = new HashMap<>(reservations);
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

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
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
		this.reservations = new HashMap<>(reservations);
	}

	@Override
	public String toString() {
		return this.id + ", " + this.name + ", " + this.description + ", " + this.units + ", " + this.price;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InventoryItem)) {
			return false;
		}

		return this.id.equals(((InventoryItem) o).id) && this.name.equals(((InventoryItem) o).name) && this.description.equals(((InventoryItem) o).description)
				&& this.units == ((InventoryItem) o).units && this.price == ((InventoryItem) o).price && this.reservations.equals(((InventoryItem) o).reservations);
	}
	
	public int getAvailableUnits() {
		int available = units - reservations.values().stream().reduce(0, Integer::sum); 
		return (available > 0 ? available : 0);
	}
	
	public void addReservation(String id, int units) {
		if (units > getAvailableUnits() || units <= 0) {
			return;
		}
		int newUnits = units + reservations.getOrDefault(id, 0);
		reservations.put(id, newUnits);
	}
}
