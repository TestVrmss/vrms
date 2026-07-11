package soft.eng.domain.model;

import java.math.BigDecimal;

import soft.eng.domain.enums.VehicleStatus;


public class Vehicle {

    private final String id;

    
    private final String plateNumber;

    
    private final String brand;

    
    private final String model;

    
    private final BigDecimal dailyRate;

    
    private VehicleStatus status;

    public Vehicle(String id, String plateNumber, String brand, String model, BigDecimal dailyRate) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = VehicleStatus.AVAILABLE;
    }

  
    public String getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
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

   
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    
    public void markAsRented() {
        this.status = VehicleStatus.RENTED;
    }

    
    public void markAsAvailable() {
        this.status = VehicleStatus.AVAILABLE;
    }
}