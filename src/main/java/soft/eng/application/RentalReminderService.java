package soft.eng.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import soft.eng.domain.model.Rental;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.NotificationService;
import soft.eng.persistence.RentalRepository;


public class RentalReminderService {

    private final RentalRepository rentalRepository;

    private final NotificationService notificationService;

   
    private final DateTimeProvider dateTimeProvider;

   
    public RentalReminderService(RentalRepository rentalRepository,
                                 NotificationService notificationService,
                                 DateTimeProvider dateTimeProvider) {
        this.rentalRepository = rentalRepository;
        this.notificationService = notificationService;
        this.dateTimeProvider = dateTimeProvider;
    }

   
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