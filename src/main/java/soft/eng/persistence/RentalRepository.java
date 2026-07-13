package soft.eng.persistence;

import java.util.List;
import java.util.Optional;
import soft.eng.domain.model.Rental;


public interface RentalRepository {

    void save(Rental rental);

    Optional<Rental> findById(String id);

    List<Rental> findAll();

    List<Rental> findActive();

    boolean existsActiveRentalForVehicle(String vehicleId);
}
