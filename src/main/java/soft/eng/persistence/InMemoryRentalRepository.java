package soft.eng.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Rental;

/**
 * In-memory implementation of rental repository.
 */
public class InMemoryRentalRepository implements RentalRepository {

    /**
     * Stores rentals by id.
     */
    private final Map<String, Rental> rentals = new HashMap<>();

    /**
     * Saves a rental.
     *
     * @param rental the rental to save
     */
    @Override
    public void save(Rental rental) {
        rentals.put(rental.getId(), rental);
    }

    /**
     * Finds a rental by id.
     *
     * @param id the rental id
     * @return an optional rental
     */
    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(rentals.get(id));
    }

    /**
     * Finds an active rental by vehicle id.
     *
     * @param vehicleId the vehicle id
     * @return an optional active rental
     */
    @Override
    public Optional<Rental> findActiveByVehicleId(String vehicleId) {
        return rentals.values()
                .stream()
                .filter(Rental::isActive)
                .filter(rental -> rental.getVehicle().getId().equals(vehicleId))
                .findFirst();
    }

    /**
     * Gets all active rentals.
     *
     * @return active rentals
     */
    @Override
    public List<Rental> findActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();

        for (Rental rental : rentals.values()) {
            if (rental.isActive()) {
                activeRentals.add(rental);
            }
        }

        return activeRentals;
    }
}