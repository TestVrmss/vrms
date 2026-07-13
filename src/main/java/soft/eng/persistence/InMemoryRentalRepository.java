package soft.eng.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import soft.eng.domain.model.Rental;


public final class InMemoryRentalRepository implements RentalRepository {
    private final Map<String, Rental> rentals = new LinkedHashMap<>();

    @Override
    public void save(Rental rental) {
        Rental validRental = Objects.requireNonNull(rental, "rental must not be null");
        rentals.put(validRental.getId(), validRental);
    }

    @Override
    public Optional<Rental> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rentals.get(id.trim()));
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals.values());
    }

    @Override
    public List<Rental> findActive() {
        return rentals.values().stream().filter(Rental::isActive).toList();
    }

    @Override
    public boolean existsActiveRentalForVehicle(String vehicleId) {
        if (vehicleId == null) {
            return false;
        }
        return rentals.values().stream()
                .anyMatch(rental -> rental.isActive() && rental.getVehicle().getId().equals(vehicleId));
    }
}
