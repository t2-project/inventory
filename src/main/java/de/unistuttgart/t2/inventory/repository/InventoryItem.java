package de.unistuttgart.t2.inventory.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Product in the inventory.
 * 
 * Each product has some describing attributes such as a name, a description and
 * a price, as well as the number of units in stock. If a user placed units of
 * product in their cart, that product has some reservations attached. The
 * actual number of unit in stock shall only ever be changed by committing
 * reservations (c.f. {@link InventoryItem#commitReservation(String)}})
 * 
 * 
 * @author maumau
 *
 */
@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id
    @Column(name = "id")
    @JsonProperty("id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(name = "name")
    @JsonProperty("name")
    private String name;
    
    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    // number units of this product. never less than the sum of all reservations.
    @Column(name = "units")
    @JsonProperty("units")
    private int units;

    @Column(name = "price")
    @JsonProperty("price")
    private double price;
    
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "item_reservation_mapping", 
      joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "reservation_id", referencedColumnName = "id")})
    @MapKey(name = "userId")
    @JsonProperty("reservations")
    private Map<String, Reservation> reservations;

    /**
     * because spring framework wants this.
     */
    public InventoryItem() {
        this("", "", "", 0, 0, new HashMap<String, Reservation>());
    }

    public InventoryItem(String id, String name, String description, int units, double price) {
        this(id, name, description, units, price, new HashMap<String, Reservation>());
    }

    @JsonCreator
    public InventoryItem(String id, String name, String description, int units, double price,
            Map<String, Reservation> reservations) {
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

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    public int getUnits() {
        return units;
    }

    /**
     * 
     * set the units. cannot be used to decrease the number of units.
     * 
     * @param units new number of unit in stock
     */
    public void setUnits(int units) {
        if (units > this.units) {
            this.units = units;
        }
    }

    public double getPrice() {
        return price;
    }

    public Map<String, Reservation> getReservations() {
        return reservations;
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
                && this.price == ((InventoryItem) o).price;
    }

    /**
     * Calculate the number of available units.
     * 
     * The number of available units is
     * {@code units in stock - sum of reserved units}
     * 
     * @return number of not yet reserved units of this product
     * @throws IllegalStateException if the reservations are too much.
     */
    @JsonIgnore
    public int getAvailableUnits() {
        int availableUnits = units - reservations.values().stream().map(r -> r.getUnits()).reduce(0, Integer::sum);
        if (availableUnits < 0) {
            throw new IllegalStateException(
                    String.format("%d units reserved, eventhough only %d are in stock", units - availableUnits, units));
        }
        return availableUnits;
    }

    /**
     * Add to or updated the products reservations.
     * 
     * If a reservation for the given {@code sessionId} already exists, the existing
     * reservation is updated, otherwise a new reservation is added. However, a
     * reservation is only added or updated reservations if enough products are
     * available.
     * 
     * @param sessionId      to identify user
     * @param unitsToReserve number of units to reserve
     * @throws IllegalArgumentException if not enough units available or otherwise
     *                                  illegal arguments
     */
    public void addReservation(String sessionId, int unitsToReserve) {
        if (unitsToReserve > getAvailableUnits() || unitsToReserve < 0) {
            throw new IllegalArgumentException(String.format(
                    "illegal amount of units to reserve: tried ro reserve %d units of product %s, but only %d are available",
                    unitsToReserve, id, getAvailableUnits()));
        }
        if (unitsToReserve == 0) {
            return;
        }
        if (reservations.containsKey(sessionId)) {
            reservations.get(sessionId).updateUnits(unitsToReserve);
        } else {
            reservations.put(sessionId, new Reservation(unitsToReserve, sessionId));
        }
    }

    /**
     * remove a reservation and decrease units in stock.
     * 
     * always use this operation to decrease the the number of unit in stock.
     * 
     * @param sessionId to identify the reservation to be committed
     */
    public void commitReservation(String sessionId) {
        if (reservations.containsKey(sessionId)) {
            units -= reservations.remove(sessionId).getUnits();
        }
    }
}