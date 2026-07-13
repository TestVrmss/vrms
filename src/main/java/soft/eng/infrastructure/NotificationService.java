package soft.eng.infrastructure;

import soft.eng.domain.model.Rental;


@FunctionalInterface
public interface NotificationService {
   
    void onRentalExpiryReminder(Rental rental);
}
