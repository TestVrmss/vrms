package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.eng.domain.enums.RentalStatus;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.domain.strategy.RentalPricingStrategy;
import soft.eng.domain.strategy.RentalValidationStrategy;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.IdGenerator;
import soft.eng.persistence.RentalRepository;
import soft.eng.persistence.VehicleRepository;


@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
        @Mock private AuthService authService;
   
        
    @Mock private RentalRepository rentalRepository;

    @Mock private VehicleRepository vehicleRepository;

    @Mock private RentalValidationStrategy validationStrategy;

    @Mock private RentalPricingStrategy pricingStrategy;

    @Mock private DateTimeProvider dateTimeProvider;

    @Mock private IdGenerator idGenerator;

    private RentalService service;

    private Customer customer;

    private Vehicle vehicle;

    private LocalDate today;


    @BeforeEach
    void setUp() {
        service = new RentalService(authService, rentalRepository, vehicleRepository,
                validationStrategy, pricingStrategy, dateTimeProvider, idGenerator);
        customer = new Customer("C", "Customer", "c@example.com", 30, true);
        vehicle = new Car("V", "Toyota", "Corolla", new BigDecimal("40"));
        today = LocalDate.of(2026, 7, 11);
    }


    @Test
    void rentsAvailableVehicle() {
        LocalDate end = today.plusDays(2);
        when(vehicleRepository.findById("V")).thenReturn(Optional.of(vehicle));
        when(rentalRepository.existsActiveRentalForVehicle("V")).thenReturn(false);
        when(dateTimeProvider.today()).thenReturn(today);
        when(idGenerator.nextId()).thenReturn("R1");

        Rental rental = service.rentVehicle(customer, "V", today, end);

        assertEquals("R1", rental.getId());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertFalse(vehicle.isAvailable());
        verify(authService).requireAuthenticated();
        verify(validationStrategy).validate(customer, vehicle, today, end, today);
        verify(vehicleRepository).save(vehicle);
        verify(rentalRepository).save(rental);
    }

    @Test
    void rejectsMissingVehicleAndDoubleBooking() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.rentVehicle(customer, "missing", today, today));

        when(vehicleRepository.findById("V")).thenReturn(Optional.of(vehicle));
        when(rentalRepository.existsActiveRentalForVehicle("V")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.rentVehicle(customer, "V", today, today));
        verify(validationStrategy, never()).validate(customer, vehicle, today, today, today);
    }

    /** Cost can be calculated without closing the rental. */
    @Test
    void calculatesRentalCost() {
        Rental rental = rental("R1");
        LocalDate returnDate = today.plusDays(2);
        when(rentalRepository.findById("R1")).thenReturn(Optional.of(rental));
        when(pricingStrategy.calculateCost(rental, returnDate)).thenReturn(new BigDecimal("120.00"));

        assertEquals(new BigDecimal("120.00"), service.calculateRentalCost("R1", returnDate));
        assertTrue(rental.isActive());
    }

    /** Returning closes the rental, makes the vehicle available and returns the cost. */
    @Test
    void returnsVehicle() {
        vehicle.rent();
        Rental rental = rental("R1");
        LocalDate returnDate = today.plusDays(3);
        when(rentalRepository.findById("R1")).thenReturn(Optional.of(rental));
        when(dateTimeProvider.today()).thenReturn(returnDate);
        when(pricingStrategy.calculateCost(rental, returnDate)).thenReturn(new BigDecimal("130.00"));

        assertEquals(new BigDecimal("130.00"), service.returnVehicle("R1"));
        assertEquals(RentalStatus.COMPLETED, rental.getStatus());
        assertTrue(vehicle.isAvailable());
        verify(rentalRepository).save(rental);
        verify(vehicleRepository).save(vehicle);
    }

    /** Active rentals are delegated to the repository. */
    @Test
    void returnsActiveRentals() {
        Rental rental = rental("R1");
        when(rentalRepository.findActive()).thenReturn(List.of(rental));
        assertEquals(List.of(rental), service.getActiveRentals());
        verify(authService).requireAuthenticated();
    }

    /** Missing rental identifiers are rejected in cost and return operations. */
    @Test
    void rejectsMissingRental() {
        when(rentalRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateRentalCost("missing", today));
        assertThrows(IllegalArgumentException.class,
                () -> service.returnVehicle("missing"));
    }

    /** Every constructor dependency is mandatory. */
    @Test
    void constructorRejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new RentalService(null, rentalRepository,
                vehicleRepository, validationStrategy, pricingStrategy, dateTimeProvider, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, null,
                vehicleRepository, validationStrategy, pricingStrategy, dateTimeProvider, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, rentalRepository,
                null, validationStrategy, pricingStrategy, dateTimeProvider, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, rentalRepository,
                vehicleRepository, null, pricingStrategy, dateTimeProvider, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, rentalRepository,
                vehicleRepository, validationStrategy, null, dateTimeProvider, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, rentalRepository,
                vehicleRepository, validationStrategy, pricingStrategy, null, idGenerator));
        assertThrows(NullPointerException.class, () -> new RentalService(authService, rentalRepository,
                vehicleRepository, validationStrategy, pricingStrategy, dateTimeProvider, null));
    }

    /** Creates a test rental with the shared vehicle. */
    private Rental rental(String id) {
        return new Rental(id, customer, vehicle, today, today.plusDays(2));
    }
}
