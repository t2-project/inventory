package de.unistuttgart.t2.inventory.repository;

import java.time.Instant;
import java.util.Date;

/**
 * A Reservation of a certain number of units.
 * 
 * <p>
 * Reservations have a {@code creationDate} such that they might be killed after
 * they exceeded their time to life.
 * 
 * @author maumau
 *
 */
public class Reservation {
    private Date creationDate;
    private int units;

    public Reservation() {
        creationDate = Date.from(Instant.now());
    }

    public Reservation(int units) {
        this();
        this.units = units;
    }

    public int getUnits() {
        return units;
    }
    
    /** 
     * 
     * set number of units and also renew the creation date. 
     * 
     * @param units new number of reserved units
     */
    public void setUnits(int units) {
        this.units = units;
        renewCreationdate();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * updates creation date to current time.
     * 
     * this extends the life span of the reservation. it exists because the time to
     * life of a reservation should always be measured according to the most recent
     * time of modification.
     */
    private void renewCreationdate() {
        creationDate = Date.from(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reservation)) {
            return false;
        }
        return this.creationDate.equals(((Reservation) o).creationDate) && this.units == ((Reservation) o).units;
    }

    @Override
    public String toString() {
        return "Reservation [creationDate=" + creationDate + ", units=" + units + "]";
    }
}
