package soft.eng.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.RentalRepository;
import soft.eng.persistence.VehicleRepository;

/**
 * Handles vehicle rental operations.
 */
public class RentalService {

    /**
     * Minimum allowed rental duration in days.
     */
    private static final long MIN_RENTAL_DAYS = 1;

    /**
     * Maximum allowed rental duration in days.
     */
    private static final long MAX_RENTAL_DAYS = 30;

    /**
     * The authentication service.
     */
    private final AuthService authService;

    /**
     * The vehicle repository.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The rental repository.
     */
    private final RentalRepository rentalRepository;

    /**
     * Creates a new rental service.
     *
     * @param authService       the authentication service
     * @param vehicleRepository the vehicle repository
     * @param rentalRepository  the rental repository
     */
    public RentalService(AuthService authService,
                         VehicleRepository vehicleRepository,
                         RentalRepository rentalRepository) {
        this.authService = authService;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * Rents a vehicle to a customer.
     *
     * @param vehicleId the vehicle id
     * @param customer  the customer
     * @param startDate the rental start date
     * @param endDate   the rental end date
     * @return the created rental record
     */
    public Rental rentVehicle(String vehicleId, Customer customer, LocalDate startDate, LocalDate endDate) {
        authService.requireLogin();
        validateRentalDates(startDate, endDate);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));

        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("Vehicle is not available.");
        }

        if (rentalRepository.findActiveByVehicleId(vehicleId).isPresent()) {
            throw new IllegalStateException("Vehicle already has an active rental.");
        }

        vehicle.markAsRented();

        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                customer,
                vehicle,
                startDate,
                endDate
        );

        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);

        return rental;
    }

    /**
     * Validates the rental dates.
     *
     * @param startDate the rental start date
     * @param endDate   the rental end date
     */
    private void validateRentalDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Rental dates must not be null.");
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (rentalDays < MIN_RENTAL_DAYS) {
            throw new IllegalArgumentException("Rental duration must be at least one day.");
        }

        if (rentalDays > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException("Rental duration must not exceed 30 days.");
        }
    }
}