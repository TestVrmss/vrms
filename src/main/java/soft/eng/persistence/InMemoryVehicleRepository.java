package soft.eng.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Vehicle;


public class InMemoryVehicleRepository implements VehicleRepository {

   
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    @Override
    public void save(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }

    
    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    
    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles.values());
    }
}