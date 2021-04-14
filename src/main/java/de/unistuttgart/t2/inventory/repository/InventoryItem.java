package de.unistuttgart.t2.inventory.repository;

import java.util.HashMap;
import java.util.Map;

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

	// number units of this product. never less than the sum of all reservations.
	@JsonProperty("units")
	private int units;

	@JsonProperty("price")
	private double price;

	// sessionIds -> reserved units
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

	public InventoryItem(String id, String name, String description, int units, double price,
			Map<String, Integer> reservations) {
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

		return this.id.equals(((InventoryItem) o).id) && this.name.equals(((InventoryItem) o).name)
				&& this.description.equals(((InventoryItem) o).description) && this.units == ((InventoryItem) o).units
				&& this.price == ((InventoryItem) o).price
				&& this.reservations.equals(((InventoryItem) o).reservations);
	}

	/**
	 * calculate the number of available units
	 * 
	 * the number of available units is units - sum of all reservations
	 * 
	 * @return number of not yet reserved units
	 * @throws IllegalStateException if the reservations are too much
	 */
	public int getAvailableUnits() {
		int available = units - reservations.values().stream().reduce(0, Integer::sum);
		if (available < 0) {
			throw new IllegalStateException(
					String.format("%d units reserved, eventhough only %d are in stock", units - available, units));
		}
		return available;
	}

	/**
	 * add or updated the products reservations
	 * 
	 * if a reservation for the given session id already exists update that
	 * reservation, otherwise add a new reservation. only adds reservations if
	 * enough products are still available
	 * 
	 * @note might changes this behaviour later.
	 * 
	 * @param id             - to identify user
	 * @param unitsToReserve - number of units to reserve
	 * @throws IllegalArgumentException if not enough units available or otherwise
	 *                                  illegal arguments
	 */
	public void addReservation(String id, int unitsToReserve) {
		if (unitsToReserve > getAvailableUnits() || unitsToReserve < 0) {
			throw new IllegalArgumentException("illegal amount of units to reserve");
		}
		if (unitsToReserve == 0) {
			return;
		}
		int updatedReservationUnits = unitsToReserve + reservations.getOrDefault(id, 0);
		reservations.put(id, updatedReservationUnits);
	}

	/**
	 * remove a reservation and decrease units.
	 * 
	 * this is capsulated to ensure that no one changes the units independently of
	 * the reservations.
	 * 
	 * @param id
	 */
	public void commitReservation(String id) {
		Integer value = reservations.remove(id);
		if (value != null) {
			units -= value;
		}
	}
}
