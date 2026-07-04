package soft.eng.persistence;

import java.util.List;
import java.util.Optional;

import soft.eng.domain.model.Vehicle;

/**
 * Defines vehicle data access operations.
 */
public interface VehicleRepository {

    /**
     * Saves a vehicle.
     *
     * @param vehicle the vehicle to save
     */
    void save(Vehicle vehicle);

    /**
     * Finds a vehicle by id.
     *
     * @param id the vehicle id
     * @return an optional vehicle
     */
    Optional<Vehicle> findById(String id);

    /**
     * Gets all vehicles.
     *
     * @return all vehicles
     */
    List<Vehicle> findAll();
}