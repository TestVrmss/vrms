package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft.eng.domain.enums.VehicleStatus;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.InMemoryManagerRepository;
import soft.eng.persistence.InMemoryRentalRepository;
import soft.eng.persistence.InMemoryVehicleRepository;

/**
 * Unit tests for RentalService.
 */
class RentalServiceTest {

    /**
     * The authentication service.
     */
    private AuthService authService;

    /**
     * The vehicle repository.
     */
    private InMemoryVehicleRepository vehicleRepository;

    /**
     * The rental repository.
     */
    private InMemoryRentalRepository rentalRepository;

    /**
     * The rental service under test.
     */
    private RentalService rentalService;

    /**
     * The test customer.
     */
    private Customer customer;

    /**
     * The test vehicle.
     */
    private Vehicle vehicle;

    /**
     * Initializes test dependencies.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryManagerRepository());
        vehicleRepository = new InMemoryVehicleRepository();
        rentalRepository = new InMemoryRentalRepository();
        rentalService = new RentalService(authService, vehicleRepository, rentalRepository);

        customer = new Customer("C1", "Ahmad Ali", 25);
        vehicle = new Vehicle("V1", "123-ABC", "Toyota", "Corolla", BigDecimal.valueOf(50));

        vehicleRepository.save(vehicle);
        authService.login("admin", "admin123");
    }

    /**
     * Tests creating a rental and changing vehicle status to rented.
     */
    @Test
    void rentVehicleShouldCreateRentalAndMarkVehicleAsRented() {
        Rental rental = rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 5)
        );

        assertNotNull(rental);
        assertNotNull(rental.getId());
        assertEquals("V1", rental.getVehicle().getId());
        assertEquals(VehicleStatus.RENTED, vehicle.getStatus());
    }

    /**
     * Tests that double booking is rejected.
     */
    @Test
    void rentVehicleTwiceShouldRejectDoubleBooking() {
        rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 5)
        );

        assertThrows(IllegalStateException.class, () -> rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 6),
                LocalDate.of(2026, 1, 8)
        ));
    }

    /**
     * Tests that rental duration cannot be less than one day.
     */
    @Test
    void rentVehicleWithZeroDaysShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 1)
        ));
    }

    /**
     * Tests that rental duration cannot exceed thirty days.
     */
    @Test
    void rentVehicleForMoreThanThirtyDaysShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 5)
        ));
    }

    /**
     * Tests that renting requires manager login.
     */
    @Test
    void rentVehicleWithoutLoginShouldThrowException() {
        authService.logout();

        assertThrows(IllegalStateException.class, () -> rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 5)
        ));
    }
}