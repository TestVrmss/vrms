package soft.eng.persistence;

import java.util.List;
import java.util.Optional;

import soft.eng.domain.model.Rental;

/**
 * Defines rental data access operations.
 */
public interface RentalRepository {

    /**
     * Saves a rental.
     *
     * @param rental the rental to save
     */
    void save(Rental rental);

    /**
     * Finds a rental by id.
     *
     * @param id the rental id
     * @return an optional rental
     */
    Optional<Rental> findById(String id);

    /**
     * Finds an active rental by vehicle id.
     *
     * @param vehicleId the vehicle id
     * @return an optional active rental
     */
    Optional<Rental> findActiveByVehicleId(String vehicleId);

    /**
     * Gets all active rentals.
     *
     * @return active rentals
     */
    List<Rental> findActiveRentals();
}