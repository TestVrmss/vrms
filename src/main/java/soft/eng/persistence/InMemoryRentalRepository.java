package soft.eng.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Rental;


public class InMemoryRentalRepository implements RentalRepository {

   
    private final Map<String, Rental> rentals = new HashMap<>();

   
    @Override
    public void save(Rental rental) {
        rentals.put(rental.getId(), rental);
    }

    
    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(rentals.get(id));
    }

    
    @Override
    public Optional<Rental> findActiveByVehicleId(String vehicleId) {
        return rentals.values()
                .stream()
                .filter(Rental::isActive)
                .filter(rental -> rental.getVehicle().getId().equals(vehicleId))
                .findFirst();
    }

 
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