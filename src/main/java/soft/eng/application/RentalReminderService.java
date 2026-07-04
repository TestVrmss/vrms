package soft.eng.application;

import java.time.LocalDate;

import soft.eng.domain.model.Rental;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.NotificationService;
import soft.eng.persistence.RentalRepository;

/**
 * Handles rental expiry reminder operations.
 */
public class RentalReminderService {

    /**
     * The rental repository.
     */
    private final RentalRepository rentalRepository;

    /**
     * The notification service.
     */
    private final NotificationService notificationService;

    /**
     * The date time provider.
     */
    private final DateTimeProvider dateTimeProvider;

    /**
     * Creates a new rental reminder service.
     *
     * @param rentalRepository   the rental repository
     * @param notificationService the notification service
     * @param dateTimeProvider   the date time provider
     */
    public RentalReminderService(RentalRepository rentalRepository,
                                 NotificationService notificationService,
                                 DateTimeProvider dateTimeProvider) {
        this.rentalRepository = rentalRepository;
        this.notificationService = notificationService;
        this.dateTimeProvider = dateTimeProvider;
    }

    /**
     * Sends reminders for rentals that expire tomorrow.
     *
     * @return number of sent reminders
     */
    public int sendExpiryRemindersForTomorrow() {
        LocalDate tomorrow = dateTimeProvider.today().plusDays(1);
        int sentReminders = 0;

        for (Rental rental : rentalRepository.findActiveRentals()) {
            if (rental.getEndDate().equals(tomorrow)) {
                notificationService.sendRentalExpiryReminder(rental);
                sentReminders++;
            }
        }

        return sentReminders;
    }
}