package soft.eng.infrastructure;

import soft.eng.domain.model.Rental;

/**
 * Defines notification operations.
 */
public interface NotificationService {

    /**
     * Sends a rental expiry reminder.
     *
     * @param rental the rental that is about to expire
     */
    void sendRentalExpiryReminder(Rental rental);
}