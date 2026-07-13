package soft.eng.domain.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;


class StandardRentalPricingStrategyTest {

    @Test
    void calculatesBaseCost() {
        Rental rental = rental(new BigDecimal("40.00"));
        StandardRentalPricingStrategy strategy = new StandardRentalPricingStrategy();
        assertEquals(new BigDecimal("120.00"), strategy.calculateCost(rental, rental.getEndDate()));
        assertEquals(StandardRentalPricingStrategy.DEFAULT_LATE_PENALTY_RATE, strategy.getLatePenaltyRate());
    }


    @Test
    void calculatesLatePenalty() {
        Rental rental = rental(new BigDecimal("40.00"));
        StandardRentalPricingStrategy strategy = new StandardRentalPricingStrategy(new BigDecimal("0.50"));
        assertEquals(new BigDecimal("160.00"), strategy.calculateCost(rental, rental.getEndDate().plusDays(2)));
        assertEquals(new BigDecimal("0.50"), strategy.getLatePenaltyRate());
    }


    @Test
    void rejectsInvalidValues() {
        Rental rental = rental(new BigDecimal("40.00"));
        assertThrows(NullPointerException.class, () -> new StandardRentalPricingStrategy(null));
        assertThrows(IllegalArgumentException.class,
                () -> new StandardRentalPricingStrategy(new BigDecimal("-0.01")));
        StandardRentalPricingStrategy strategy = new StandardRentalPricingStrategy();
        assertThrows(NullPointerException.class, () -> strategy.calculateCost(null, LocalDate.now()));
        assertThrows(NullPointerException.class, () -> strategy.calculateCost(rental, null));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.calculateCost(rental, rental.getStartDate().minusDays(1)));
    }

    /** Creates a three-day rental. */
    private static Rental rental(BigDecimal dailyRate) {
        Customer customer = new Customer("C", "Customer", "c@example.com", 30, false);
        Car vehicle = new Car("V", "Toyota", "Yaris", dailyRate);
        return new Rental("R", customer, vehicle,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12));
    }
}
