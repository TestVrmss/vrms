package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.InMemoryManagerRepository;
import soft.eng.persistence.InMemoryVehicleRepository;


class VehicleCatalogServiceTest {

   
    private AuthService authService;

   
    private InMemoryVehicleRepository vehicleRepository;

   
    private VehicleCatalogService vehicleCatalogService;

    
    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryManagerRepository());
        vehicleRepository = new InMemoryVehicleRepository();
        vehicleCatalogService = new VehicleCatalogService(authService, vehicleRepository);
    }

   
    @Test
    void getAvailableVehiclesShouldReturnOnlyAvailableVehicles() {
        authService.login("admin", "admin123");

        Vehicle availableVehicle = new Vehicle("V1", "111-A", "Toyota", "Corolla", BigDecimal.valueOf(50));
        Vehicle rentedVehicle = new Vehicle("V2", "222-B", "Honda", "Civic", BigDecimal.valueOf(60));
        rentedVehicle.markAsRented();

        vehicleRepository.save(availableVehicle);
        vehicleRepository.save(rentedVehicle);

        List<Vehicle> availableVehicles = vehicleCatalogService.getAvailableVehicles();

        assertEquals(1, availableVehicles.size());
        assertEquals("V1", availableVehicles.get(0).getId());
    }

    
    @Test
    void getAvailableVehiclesWithoutLoginShouldThrowException() {
        assertThrows(IllegalStateException.class, () -> vehicleCatalogService.getAvailableVehicles());
    }
}