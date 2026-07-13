package soft.eng.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import soft.eng.domain.enums.VehicleStatus;
import soft.eng.domain.enums.VehicleType;


public abstract class Vehicle {
    private final String id;
    private final String brand;
    private final String model;
    private final BigDecimal dailyRate;
    private VehicleStatus status;

    
    protected Vehicle(String id, String brand, String model, BigDecimal dailyRate) {
        this(id, brand, model, dailyRate, VehicleStatus.AVAILABLE);
    }

    protected Vehicle(String id, String brand, String model, BigDecimal dailyRate, VehicleStatus status) {
        this.id = requireText(id, "id");
        this.brand = requireText(brand, "brand");
        this.model = requireText(model, "model");
        if (dailyRate == null || dailyRate.signum() <= 0) {
            throw new IllegalArgumentException("dailyRate must be positive");
        }
        this.dailyRate = dailyRate;
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public abstract VehicleType getType();

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public void rent() {
        if (!isAvailable()) {
            throw new IllegalStateException("vehicle is not available");
        }
        status = VehicleStatus.RENTED;
    }

    public void makeAvailable() {
        status = VehicleStatus.AVAILABLE;
    }

    public void sendToMaintenance() {
        status = VehicleStatus.MAINTENANCE;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vehicle vehicle)) {
            return false;
        }
        return id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getType() + "{" + id + ", " + brand + " " + model + ", rate=" + dailyRate + ", status=" + status + "}";
    }
}
