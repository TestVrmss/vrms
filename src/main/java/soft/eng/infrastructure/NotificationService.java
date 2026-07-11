package soft.eng.infrastructure;

import soft.eng.domain.model.Rental;


public interface NotificationService {

   
    void sendRentalExpiryReminder(Rental rental);
}