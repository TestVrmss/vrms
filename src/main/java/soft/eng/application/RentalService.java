package soft.eng.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.domain.strategy.RentalPricingStrategy;
import soft.eng.domain.strategy.RentalValidationStrategy;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.IdGenerator;
import soft.eng.persistence.RentalRepository;
import soft.eng.persistence.VehicleRepository;


public final class RentalService {

    private final AuthService authService;

    private final RentalRepository rentalRepository;

    private final VehicleRepository vehicleRepository;

    private final RentalValidationStrategy validationStrategy;

    private final RentalPricingStrategy pricingStrategy;

    private final DateTimeProvider dateTimeProvider;

    private final IdGenerator idGenerator;

  
    public RentalService(AuthService authService, RentalRepository rentalRepository,
                         VehicleRepository vehicleRepository, RentalValidationStrategy validationStrategy,
                         RentalPricingStrategy pricingStrategy, DateTimeProvider dateTimeProvider,
                         IdGenerator idGenerator) {
        this.authService = Objects.requireNonNull(authService, "authService must not be null");
        this.rentalRepository = Objects.requireNonNull(rentalRepository, "rentalRepository must not be null");
        this.vehicleRepository = Objects.requireNonNull(vehicleRepository, "vehicleRepository must not be null");
        this.validationStrategy = Objects.requireNonNull(validationStrategy, "validationStrategy must not be null");
        this.pricingStrategy = Objects.requireNonNull(pricingStrategy, "pricingStrategy must not be null");
        this.dateTimeProvider = Objects.requireNonNull(dateTimeProvider, "dateTimeProvider must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
    }

  
    public Rental rentVehicle(Customer customer, String vehicleId, LocalDate startDate, LocalDate endDate) {
        authService.requireAuthenticated();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("vehicle not found: " + vehicleId));
        if (rentalRepository.existsActiveRentalForVehicle(vehicle.getId())) {
            throw new IllegalStateException("vehicle already has an active rental");
        }
        validationStrategy.validate(customer, vehicle, startDate, endDate, dateTimeProvider.today());
        Rental rental = new Rental(idGenerator.nextId(), customer, vehicle, startDate, endDate);
        vehicle.rent();
        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);
        return rental;
    }

   
    public BigDecimal calculateRentalCost(String rentalId, LocalDate proposedReturnDate) {
        authService.requireAuthenticated();
        Rental rental = findRental(rentalId);
        return pricingStrategy.calculateCost(rental, proposedReturnDate);
    }

   
    public BigDecimal returnVehicle(String rentalId) {
        authService.requireAuthenticated();
        Rental rental = findRental(rentalId);
        LocalDate returnDate = dateTimeProvider.today();
        BigDecimal total = pricingStrategy.calculateCost(rental, returnDate);
        rental.close(returnDate);
        rental.getVehicle().makeAvailable();
        rentalRepository.save(rental);
        vehicleRepository.save(rental.getVehicle());
        return total;
    }

   
    public List<Rental> getActiveRentals() {
        authService.requireAuthenticated();
        return rentalRepository.findActive();
    }

    private Rental findRental(String rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("rental not found: " + rentalId));
    }
}
