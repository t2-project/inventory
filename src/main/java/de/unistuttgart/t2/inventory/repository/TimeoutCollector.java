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
 * periodically checks the repository and deletes entities whose ttl expired.
 * 
 * actually you can mongo native attach an expiry date to documents, but i don't
 * how the repository interface works (and wether each item is its own document)
 * thus manual deletion.
 * 
 * @author maumau
 *
 */
@Component
public class TimeoutCollector {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${TTL:20}") // in seconds
	long TTL;
	@Value("${taskRate:20000}") // in milliseconds
	int taskRate;

	@Autowired
	ProductRepository repository;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@PostConstruct
	public void schedulePeriodically() {
		taskScheduler.scheduleAtFixedRate(new RunnableTask(), taskRate);
	}

	class RunnableTask implements Runnable {

		@Override
		public void run() {
			Map<String, List<String>> expiredItems = getExpiredItems();
			for (String id : expiredItems.keySet()) {
				deleteItem(id, expiredItems.get(id));
			}
		}

		/**
		 * get all ids of all expired items.
		 * 
		 * get first and delete later because i want to lock db as little as possible.
		 * 
		 * @return
		 */
		private Map<String, List<String>> getExpiredItems() {
			Map<String, List<String>> rval = new HashMap<>();
			List<InventoryItem> items = repository.findAll();
			Date now = Date.from(Instant.now().minusSeconds(TTL));
			for (InventoryItem item : items) {
				for(String key: item.getReservations().keySet()) {
					// check creation date vs. TTL
					if (item.getReservations().get(key).getCreationDate().before(now)) { // fix duration, but that's the gist
						List<String> foo = rval.getOrDefault(item.getId(), new ArrayList<>());
						foo.add(key);
						rval.put(item.getId(), foo);
					}
				}
			}
			return rval;
		}

		/**
		 * delete item from db.
		 * 
		 * this is a stand alone method because i do not know how save delete is, but i
		 * know that annotating a method with 'transactional' makes it save.
		 * 
		 * @param id of item to be deleted.
		 */
		@Transactional
		private void deleteItem(String id, List<String> reservations) {
			Optional<InventoryItem> item = repository.findById(id);
			if (item.isPresent()) {
				for (String sessionId : reservations) {
					item.get().getReservations().remove(sessionId);					
				}
				repository.save(item.get());
			}
			LOG.info(String.format("delete %d expired reservations for product %s", reservations.size(), id));
		}
	}
}
