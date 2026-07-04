package soft.eng.application;

import java.util.ArrayList;
import java.util.List;

import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.VehicleRepository;

/**
 * Handles vehicle catalog operations.
 */
public class VehicleCatalogService {

    /**
     * The authentication service.
     */
    private final AuthService authService;

    /**
     * The vehicle repository.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Creates a new vehicle catalog service.
     *
     * @param authService       the authentication service
     * @param vehicleRepository the vehicle repository
     */
    public VehicleCatalogService(AuthService authService, VehicleRepository vehicleRepository) {
        this.authService = authService;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Gets only available vehicles.
     *
     * @return available vehicles
     */
    public List<Vehicle> getAvailableVehicles() {
        authService.requireLogin();

        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.isAvailable()) {
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }
}