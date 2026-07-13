package soft.eng.domain.model;

import java.math.BigDecimal;
import soft.eng.domain.enums.VehicleType;

public final class ElectricVehicle extends Vehicle {
    private int batteryLevel;

   
    public ElectricVehicle(String id, String brand, String model, BigDecimal dailyRate, int batteryLevel) {
        super(id, brand, model, dailyRate);
        updateBatteryLevel(batteryLevel);
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

   
    public void updateBatteryLevel(int batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("batteryLevel must be between 0 and 100");
        }
        this.batteryLevel = batteryLevel;
    }

    @Override 
    public VehicleType getType() { return VehicleType.ELECTRIC; }
}
