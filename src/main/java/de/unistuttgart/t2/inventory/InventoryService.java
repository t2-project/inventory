package de.unistuttgart.t2.inventory;

import de.unistuttgart.t2.inventory.repository.InventoryItem;
import de.unistuttgart.t2.inventory.repository.ProductRepository;
import de.unistuttgart.t2.inventory.repository.Reservation;
import de.unistuttgart.t2.inventory.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Interactions with the product repository that involve reservations.
 *
 * @author maumau
 */
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    public InventoryService(@Autowired ProductRepository productRepository,
        @Autowired ReservationRepository reservationRepository) {
        assert productRepository != null && reservationRepository != null;
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * commit reservations associated with given sessionId.
     *
     * @param sessionId to identify the reservations to delete
     */
    public void handleSagaAction(String sessionId) {
        List<InventoryItem> items = productRepository.findAll();
        for (InventoryItem item : items) {
            item.commitReservation(sessionId);
        }
        productRepository.saveAll(items);

        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.getUserId().equals(sessionId)) {
                reservationRepository.delete(reservation);
            }
        }
    }

    /**
     * delete reservations of cancelled order from repository.
     *
     * @param sessionId to identify which reservations to delete
     */
    public void handleSagaCompensation(String sessionId) {
        List<InventoryItem> items = productRepository.findAll();
        for (InventoryItem item : items) {
            item.deleteReservation(sessionId);

        }
        productRepository.saveAll(items);

        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.getUserId().equals(sessionId)) {
                reservationRepository.delete(reservation);
            }
        }
    }

    /**
     * attach a reservation for the given session to the given item.
     *
     * @param productId products to reserve of
     * @param sessionId user to reserve for
     * @param units     amount to reserve
     * @return the item where the reservation was attached
     * @throws NoSuchElementException   if the product does not exist
     * @throws IllegalArgumentException if any parameter is null
     */
    public InventoryItem makeReservation(String productId, String sessionId, int units) throws NoSuchElementException {
        if (productId == null || sessionId == null || units < 0) {
            throw new IllegalArgumentException(
                "productId : " + productId + ", sessionId : " + sessionId + ", units : " + units);
        }
        InventoryItem item = productRepository.findById(productId).orElseThrow(
            () -> new NoSuchElementException(String.format("product with id %s not found", productId)));

        item.addReservation(sessionId, units);
        return productRepository.save(item);
    }
}
