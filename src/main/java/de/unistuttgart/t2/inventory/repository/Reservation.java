package de.unistuttgart.t2.inventory.repository;

import java.time.Instant;
import java.util.Date;

public class Reservation {
	private Date creationDate;
	private int units;
	
	public Reservation() {
		super();
		creationDate = Date.from(Instant.now());
	}

	public Reservation(int units) {
		this();
		this.units = units;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void renewCreationdate() {
		creationDate = Date.from(Instant.now());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Reservation)) {
			return false;
		}

		return this.creationDate.equals(((Reservation) o).creationDate) && this.units == ((Reservation) o).units;
	}
}

