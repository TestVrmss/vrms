package soft.eng.application;

import java.util.List;
import java.util.Objects;
import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.VehicleRepository;


public final class VehicleCatalogService {

    private final AuthService authService;

    private final VehicleRepository vehicleRepository;

    
    public VehicleCatalogService(AuthService authService, VehicleRepository vehicleRepository) {
        this.authService = Objects.requireNonNull(authService, "authService must not be null");
        this.vehicleRepository = Objects.requireNonNull(vehicleRepository, "vehicleRepository must not be null");
    }

    public List<Vehicle> getAvailableVehicles() {
        authService.requireAuthenticated();
        return vehicleRepository.findAvailable();
    }


    public List<Vehicle> getAllVehicles() {
        authService.requireAuthenticated();
        return vehicleRepository.findAll();
    }
}
