package soft.eng.domain.strategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import soft.eng.domain.enums.VehicleType;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.ElectricVehicle;
import soft.eng.domain.model.Vehicle;


public final class DefaultRentalValidationStrategy implements RentalValidationStrategy {

    private final int maximumRentalDays;

    private final int motorcycleMinimumAge;

    private final int minimumElectricBattery;


    public DefaultRentalValidationStrategy() {
        this(30, 18, 20);
    }

    
    public DefaultRentalValidationStrategy(int maximumRentalDays, int motorcycleMinimumAge,
                                           int minimumElectricBattery) {
        if (maximumRentalDays <= 0) {
            throw new IllegalArgumentException("maximumRentalDays must be positive");
        }
        if (motorcycleMinimumAge < 0) {
            throw new IllegalArgumentException("motorcycleMinimumAge must not be negative");
        }
        if (minimumElectricBattery < 0 || minimumElectricBattery > 100) {
            throw new IllegalArgumentException("minimumElectricBattery must be between 0 and 100");
        }
        this.maximumRentalDays = maximumRentalDays;
        this.motorcycleMinimumAge = motorcycleMinimumAge;
        this.minimumElectricBattery = minimumElectricBattery;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Customer customer, Vehicle vehicle, LocalDate startDate,
                         LocalDate endDate, LocalDate currentDate) {
        Objects.requireNonNull(customer, "customer must not be null");
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        Objects.requireNonNull(currentDate, "currentDate must not be null");

        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("vehicle is not available");
        }
        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("startDate must not be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }

        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (rentalDays > maximumRentalDays) {
            throw new IllegalArgumentException("rental period exceeds " + maximumRentalDays + " days");
        }

        if (vehicle.getType() == VehicleType.MOTORCYCLE && customer.getAge() < motorcycleMinimumAge) {
            throw new IllegalArgumentException("customer is too young to rent a motorcycle");
        }
        if (vehicle.getType() == VehicleType.TRUCK && !customer.hasSpecialLicense()) {
            throw new IllegalArgumentException("a special license is required for trucks");
        }
        if (vehicle instanceof ElectricVehicle electricVehicle
                && electricVehicle.getBatteryLevel() < minimumElectricBattery) {
            throw new IllegalStateException("electric vehicle battery is too low");
        }
    }


    public int getMaximumRentalDays() { return maximumRentalDays; }

    public int getMotorcycleMinimumAge() { return motorcycleMinimumAge; }

    public int getMinimumElectricBattery() { return minimumElectricBattery; }
}
