package de.unistuttgart.t2.inventory.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodically checks all reservations and deletes those whose time to life has been exceeded.
 * 
 * <p>
 * (apparently there is a mongo native attach on expiry date to documents, but i didn't find anything on
 * whether this also works with the spring repository interface. thus the manual deletion.)
 * 
 * @author maumau
 *
 */
@Component
public class TimeoutCollector {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private long TTL;
	private int taskRate;

	@Autowired
	ProductRepository repository;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
    /**
     * Create collector.
     * 
     * @param TTL       the cart entries' time to live in seconds
     * @param taskRate  rate at which the collector checks the repo in milliseconds
     */
    @Autowired
    public TimeoutCollector(@Value("${t2.inventory.TTL:0}") long TTL, @Value("${t2.inventory.taskRate:0}") int taskRate) {
        this.TTL = TTL;
        this.taskRate = taskRate;
    }

	/**
	 * Schedule the task to check reservations and delete them if necessary.
	 * 
	 * <p>
	 * If either the TTL or the taskRate is 0, no task will be scheduled.
	 */
	@PostConstruct
	public void schedulePeriodically() {
		if (taskRate > 0) {
			taskScheduler.scheduleAtFixedRate(new RecervationCheckAndDeleteTask(), taskRate);
		}
	}

	/**
	 * The Task that does the actual checking and deleting of reservations.
	 * 
	 * @author maumau
	 *
	 */
	protected class RecervationCheckAndDeleteTask implements Runnable {

		@Override
		public void run() {
			Map<String, List<String>> expiredReservation = getExpiredReservations();
			LOG.info(String.format("found %d expired reservations", expiredReservation.size()));
			
			for (String productId : expiredReservation.keySet()) {
				deleteReservation(productId, expiredReservation.get(productId));
			}
		}

		/**
		 * Get all ids of all expired items.
		 * 
		 * <p>
		 * The get step is separated from the delete step because i want to lock the db as little as possible and need not do it for getting the ids.
		 * If any user updates their reservation (and thereby enlongates it's ttl) it's their problem. The reservation will be deleted anyway.
		 * 
		 * @return expired reservations by product
		 */
		private Map<String, List<String>> getExpiredReservations() {
			Map<String, List<String>> reservationsByProductId = new HashMap<>();
			
			List<InventoryItem> items = repository.findAll();
			Date now = Date.from(Instant.now().minusSeconds(TTL));
			
			for (InventoryItem item : items) {
				for(String key: item.getReservations().keySet()) {
					// check creation date vs. TTL
					if (item.getReservations().get(key).getCreationDate().before(now)) { 
						List<String> deadReservations = reservationsByProductId.getOrDefault(item.getId(), new ArrayList<>());
						deadReservations.add(key);
						reservationsByProductId.put(item.getId(), deadReservations);
					}
				}
			}
			return reservationsByProductId;
		}

		/**
		 * Delete expired reservations from products.
		 * 
		 * @param productId id of the product that has expired reservations
		 * @param reservations expired reservations of given product
		 */
		@Transactional
		private void deleteReservation(String productId, List<String> reservations) {
			Optional<InventoryItem> item = repository.findById(productId);
			if (item.isPresent()) {
				for (String sessionId : reservations) {
					item.get().getReservations().remove(sessionId);					
				}
				repository.save(item.get());
			}
			LOG.info(String.format("delete %d expired reservations from product %s", reservations.size(), productId));
		}
	}
}
