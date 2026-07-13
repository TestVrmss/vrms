package soft.eng.domain.strategy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.ElectricVehicle;
import soft.eng.domain.model.Motorcycle;
import soft.eng.domain.model.Truck;
import soft.eng.domain.model.Vehicle;

class DefaultRentalValidationStrategyTest {

        private LocalDate today;

        private Customer adult;

        private DefaultRentalValidationStrategy strategy;


        @BeforeEach
    void setUp() {
        today = LocalDate.of(2026, 7, 11);
        adult = new Customer("C", "Adult", "adult@example.com", 25, true);
        strategy = new DefaultRentalValidationStrategy();
    }


    @Test
    void acceptsValidRequests() {
        assertDoesNotThrow(() -> strategy.validate(adult, car(), today, today.plusDays(2), today));
        assertDoesNotThrow(() -> strategy.validate(adult,
                new Motorcycle("M", "Honda", "CB", new BigDecimal("20")), today, today, today));
        assertDoesNotThrow(() -> strategy.validate(adult,
                new Truck("T", "Volvo", "FM", new BigDecimal("100")), today, today, today));
        assertDoesNotThrow(() -> strategy.validate(adult,
                new ElectricVehicle("E", "Tesla", "3", new BigDecimal("80"), 80), today, today, today));
        assertEquals(30, strategy.getMaximumRentalDays());
        assertEquals(18, strategy.getMotorcycleMinimumAge());
        assertEquals(20, strategy.getMinimumElectricBattery());
    }


    @Test
    void rejectsInvalidGeneralRequests() {
        Vehicle unavailable = car();
        unavailable.rent();
        assertThrows(IllegalStateException.class,
                () -> strategy.validate(adult, unavailable, today, today, today));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validate(adult, car(), today.minusDays(1), today, today));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validate(adult, car(), today.plusDays(1), today, today));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validate(adult, car(), today, today.plusDays(30), today));
    }


    @Test
    void rejectsInvalidTypeSpecificRequests() {
        Customer minor = new Customer("M", "Minor", "minor@example.com", 17, false);
        Customer noLicense = new Customer("N", "No License", "n@example.com", 30, false);
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validate(minor,
                        new Motorcycle("M", "Honda", "CB", new BigDecimal("20")), today, today, today));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validate(noLicense,
                        new Truck("T", "Volvo", "FM", new BigDecimal("100")), today, today, today));
        assertThrows(IllegalStateException.class,
                () -> strategy.validate(adult,
                        new ElectricVehicle("E", "Tesla", "3", new BigDecimal("80"), 19), today, today, today));
    }


    @Test
    void rejectsInvalidConfigurationAndNulls() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultRentalValidationStrategy(0, 18, 20));
        assertThrows(IllegalArgumentException.class, () -> new DefaultRentalValidationStrategy(30, -1, 20));
        assertThrows(IllegalArgumentException.class, () -> new DefaultRentalValidationStrategy(30, 18, -1));
        assertThrows(IllegalArgumentException.class, () -> new DefaultRentalValidationStrategy(30, 18, 101));
        assertThrows(NullPointerException.class, () -> strategy.validate(null, car(), today, today, today));
        assertThrows(NullPointerException.class, () -> strategy.validate(adult, null, today, today, today));
        assertThrows(NullPointerException.class, () -> strategy.validate(adult, car(), null, today, today));
        assertThrows(NullPointerException.class, () -> strategy.validate(adult, car(), today, null, today));
        assertThrows(NullPointerException.class, () -> strategy.validate(adult, car(), today, today, null));
    }

    /** Creates a standard car. */
    private static Vehicle car() {
        return new Car("C", "Toyota", "Corolla", new BigDecimal("40"));
    }
}
