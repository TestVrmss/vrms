package soft.eng.domain.strategy;

import java.time.LocalDate;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Vehicle;


public interface RentalValidationStrategy {
  
    void validate(Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate, LocalDate currentDate);
}
