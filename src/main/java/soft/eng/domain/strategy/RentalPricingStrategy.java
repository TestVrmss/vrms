package soft.eng.domain.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import soft.eng.domain.model.Rental;


public interface RentalPricingStrategy {
   
    BigDecimal calculateCost(Rental rental, LocalDate actualReturnDate);
}
