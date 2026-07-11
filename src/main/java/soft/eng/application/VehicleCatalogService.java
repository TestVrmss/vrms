package soft.eng.application;

import java.util.ArrayList;
import java.util.List;

import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.VehicleRepository;


public class VehicleCatalogService {

    
    private final AuthService authService;

    private final VehicleRepository vehicleRepository;

   
    public VehicleCatalogService(AuthService authService, VehicleRepository vehicleRepository) {
        this.authService = authService;
        this.vehicleRepository = vehicleRepository;
    }

    
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