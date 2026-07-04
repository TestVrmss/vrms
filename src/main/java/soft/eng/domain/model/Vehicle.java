package soft.eng.domain.model;

import java.math.BigDecimal;

import soft.eng.domain.enums.VehicleStatus;

/**
 * Represents a vehicle that can be rented by customers.
 */
public class Vehicle {

    /**
     * The vehicle unique identifier.
     */
    private final String id;

    /**
     * The vehicle plate number.
     */
    private final String plateNumber;

    /**
     * The vehicle brand.
     */
    private final String brand;

    /**
     * The vehicle model.
     */
    private final String model;

    /**
     * The daily rental rate.
     */
    private final BigDecimal dailyRate;

    /**
     * The current vehicle status.
     */
    private VehicleStatus status;

    /**
     * Creates a new available vehicle.
     *
     * @param id          the vehicle id
     * @param plateNumber the vehicle plate number
     * @param brand       the vehicle brand
     * @param model       the vehicle model
     * @param dailyRate   the daily rental rate
     */
    public Vehicle(String id, String plateNumber, String brand, String model, BigDecimal dailyRate) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = VehicleStatus.AVAILABLE;
    }

    /**
     * Gets the vehicle id.
     *
     * @return the vehicle id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the vehicle plate number.
     *
     * @return the plate number
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * Gets the vehicle brand.
     *
     * @return the vehicle brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Gets the vehicle model.
     *
     * @return the vehicle model
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the vehicle daily rental rate.
     *
     * @return the daily rental rate
     */
    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    /**
     * Gets the current vehicle status.
     *
     * @return the vehicle status
     */
    public VehicleStatus getStatus() {
        return status;
    }

    /**
     * Checks whether the vehicle is available.
     *
     * @return true if the vehicle is available, otherwise false
     */
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    /**
     * Marks the vehicle as rented.
     */
    public void markAsRented() {
        this.status = VehicleStatus.RENTED;
    }

    /**
     * Marks the vehicle as available.
     */
    public void markAsAvailable() {
        this.status = VehicleStatus.AVAILABLE;
    }
}