package soft.eng.persistence;

import java.util.List;
import java.util.Optional;

import soft.eng.domain.model.Rental;


public interface RentalRepository {

    
    void save(Rental rental);

    
    Optional<Rental> findById(String id);

   
    Optional<Rental> findActiveByVehicleId(String vehicleId);

    List<Rental> findActiveRentals();
}