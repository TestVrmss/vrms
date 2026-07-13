package soft.eng.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import soft.eng.domain.model.Vehicle;


public final class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<String, Vehicle> vehicles = new LinkedHashMap<>();

    @Override
    public void save(Vehicle vehicle) {
        Vehicle validVehicle = Objects.requireNonNull(vehicle, "vehicle must not be null");
        vehicles.put(validVehicle.getId(), validVehicle);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(vehicles.get(id.trim()));
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles.values());
    }

    @Override
    public List<Vehicle> findAvailable() {
        return vehicles.values().stream().filter(Vehicle::isAvailable).toList();
    }
}
