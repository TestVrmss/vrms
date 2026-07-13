package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleCatalogServiceTest {

    @Mock private AuthService authService;

    @Mock private VehicleRepository vehicleRepository;


    @Test
    void returnsAvailableVehicles() {
        Vehicle car = new Car("1", "Toyota", "Corolla", new BigDecimal("40"));
        when(vehicleRepository.findAvailable()).thenReturn(List.of(car));
        VehicleCatalogService service = new VehicleCatalogService(authService, vehicleRepository);

        assertEquals(List.of(car), service.getAvailableVehicles());
        verify(authService).requireAuthenticated();
    }


    @Test
    void returnsAllVehicles() {
        Vehicle car = new Car("1", "Toyota", "Corolla", new BigDecimal("40"));
        when(vehicleRepository.findAll()).thenReturn(List.of(car));
        VehicleCatalogService service = new VehicleCatalogService(authService, vehicleRepository);

        assertEquals(List.of(car), service.getAllVehicles());
        verify(authService).requireAuthenticated();
    }


    @Test
    void constructorRejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new VehicleCatalogService(null, vehicleRepository));
        assertThrows(NullPointerException.class, () -> new VehicleCatalogService(authService, null));
    }
}
