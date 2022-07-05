package de.unistuttgart.t2.inventory.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Periodically checks all reservations and deletes those whose time to life has been exceeded. TODO : ensure that only
 * reservations that are not part of a running saga are deleted
 * 
 * @author maumau
 */
@Component
public class TimeoutCollector {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /** in seconds */
    private long TTL;
    /** in milliseconds */
    private int taskRate;

    private final ReservationRepository repository;
    private final ProductRepository itemRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    /**
     * Create collector.
     * 
     * @param TTL      the cart entries' time to live in seconds
     * @param taskRate rate at which the collector checks the repo in milliseconds
     */
    @Autowired
    public TimeoutCollector(@Value("${t2.inventory.TTL:0}") final long TTL,
        @Value("${t2.inventory.taskRate:0}") final int taskRate,
        @Autowired final ThreadPoolTaskScheduler taskScheduler, @Autowired final ReservationRepository repository,
        @Autowired final ProductRepository itemRepository) {
        assert (TTL >= 0 && taskRate >= 0 && taskScheduler != null && repository != null && itemRepository != null);
        this.TTL = TTL;
        this.taskRate = taskRate;
        this.taskScheduler = taskScheduler;
        this.repository = repository;
        this.itemRepository = itemRepository;

    }

    /**
     * Schedule the task to check reservations and delete them if necessary.
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
     * The Task that does the actual checking and deleting of reservations. TODO how do i prevent this from collection
     * 'in progress' sagas? TODO 'father less' reservations are only caused when orchestrator is down. I could flag the
     * reservations as 'PENDING' (not yet ordered) 'PROCESSING' (saga runs) or 'DONE' (you may delete) and frequently
     * delete 'DONE', scarcely delete 'PENDING' (i.e. after cookie death) and report 'PROCESSING' after some time as
     * major erro...
     * 
     * @author maumau
     */
    protected class RecervationCheckAndDeleteTask implements Runnable {

        @Override
        public void run() {
            List<Reservation> items = repository.findAll();
            Date latestDateAlive = Date.from(Instant.now().minusSeconds(TTL));

            Collection<Reservation> rval = items.stream().filter((Reservation r) -> {
                return r.getCreationDate().before(latestDateAlive);
            }).collect(Collectors.toSet());

            LOG.info(String.format("found %d expired reservations", rval.size()));

            deleteAtItems(rval);
            repository.deleteAll(rval);
        }

        public void deleteAtItems(Collection<Reservation> rs) {
            for (Reservation r : rs) {

                InventoryItem i = itemRepository.findById(r.item.getId()).get();
                i.deleteReservation(r.getUserId());
                itemRepository.save(i);
            }
        }
    }
}
