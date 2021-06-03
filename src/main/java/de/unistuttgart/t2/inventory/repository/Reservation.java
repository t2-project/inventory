package de.unistuttgart.t2.inventory.repository;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date creationDate;
    @Column(name = "units")
    private int units;
    
    @Column(name = "userId")
    private String userId;

    public Reservation() {
        this(0,Date.from(Instant.now()), "");
    }

    public Reservation(int units, String userId) {
        this(units, Date.from(Instant.now()), userId);
    }

    protected Reservation(int units, Date date, String userId) {
        super();
        this.units = units;
        this.creationDate = date;
        this.userId = userId;
    }

    
    public int getUnits() {
        return units;
    }
    
    /** 
     * 
     * update number of units and also renew the creation date. 
     * 
     * @param update additionally reserved units
     */
    public void updateUnits(int update) {
        this.units = units + update;
        renewCreationdate();
    }

    public Date getCreationDate() {
        return creationDate;
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
