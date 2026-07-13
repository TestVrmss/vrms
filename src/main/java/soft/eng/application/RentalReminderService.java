package soft.eng.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import soft.eng.domain.model.Rental;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.NotificationService;
import soft.eng.persistence.RentalRepository;


public final class RentalReminderService {
    private final RentalRepository rentalRepository;
    private final DateTimeProvider dateTimeProvider;
    private final List<NotificationService> observers = new CopyOnWriteArrayList<>();

    
    public RentalReminderService(RentalRepository rentalRepository, DateTimeProvider dateTimeProvider) {
        this.rentalRepository = Objects.requireNonNull(rentalRepository, "rentalRepository must not be null");
        this.dateTimeProvider = Objects.requireNonNull(dateTimeProvider, "dateTimeProvider must not be null");
    }

    
    public void registerObserver(NotificationService observer) {
        NotificationService validObserver = Objects.requireNonNull(observer, "observer must not be null");
        if (!observers.contains(validObserver)) {
            observers.add(validObserver);
        }
    }

    public boolean removeObserver(NotificationService observer) {
        return observers.remove(observer);
    }

    public int observerCount() {
        return observers.size();
    }

    
    public int checkExpiringRentals(int daysBeforeExpiry) {
        if (daysBeforeExpiry < 0) {
            throw new IllegalArgumentException("daysBeforeExpiry must not be negative");
        }
        LocalDate today = dateTimeProvider.today();
        int notifications = 0;
        for (Rental rental : rentalRepository.findActive()) {
            if (rental.expiresWithin(today, daysBeforeExpiry)) {
                for (NotificationService observer : observers) {
                    observer.onRentalExpiryReminder(rental);
                    notifications++;
                }
            }
        }
        return notifications;
    }
}
