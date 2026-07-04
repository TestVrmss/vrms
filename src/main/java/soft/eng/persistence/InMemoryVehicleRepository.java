package soft.eng.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Vehicle;

/**
 * In-memory implementation of vehicle repository.
 */
public class InMemoryVehicleRepository implements VehicleRepository {

    /**
     * Stores vehicles by id.
     */
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    /**
     * Saves a vehicle.
     *
     * @param vehicle the vehicle to save
     */
    @Override
    public void save(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }

    /**
     * Finds a vehicle by id.
     *
     * @param id the vehicle id
     * @return an optional vehicle
     */
    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    /**
     * Gets all vehicles.
     *
     * @return all vehicles
     */
    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles.values());
    }
}