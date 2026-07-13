package soft.eng.domain.model;

import java.math.BigDecimal;
import soft.eng.domain.enums.VehicleType;

public final class Car extends Vehicle {
    public Car(String id, String brand, String model, BigDecimal dailyRate) {
        super(id, brand, model, dailyRate);
    }
    @Override
     public VehicleType getType() { return VehicleType.CAR; }
}
    