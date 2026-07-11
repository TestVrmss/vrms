package soft.eng.persistence;

import java.util.List;
import java.util.Optional;

import soft.eng.domain.model.Vehicle;


public interface VehicleRepository {

   
    void save(Vehicle vehicle);

   
    Optional<Vehicle> findById(String id);

   
    List<Vehicle> findAll();
}