package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.NotificationService;
import soft.eng.persistence.InMemoryRentalRepository;

/**
 * Unit tests for RentalReminderService.
 */
@ExtendWith(MockitoExtension.class)
class RentalReminderServiceTest {

    /**
     * Mock notification service.
     */
    @Mock
    private NotificationService notificationService;

    /**
     * Mock date time provider.
     */
    @Mock
    private DateTimeProvider dateTimeProvider;

    /**
     * The rental repository.
     */
    private InMemoryRentalRepository rentalRepository;

    /**
     * The rental reminder service under test.
     */
    private RentalReminderService rentalReminderService;

    /**
     * Initializes test dependencies.
     */
    @BeforeEach
    void setUp() {
        rentalRepository = new InMemoryRentalRepository();
        rentalReminderService = new RentalReminderService(
                rentalRepository,
                notificationService,
                dateTimeProvider
        );
    }

    /**
     * Tests that a reminder is sent for rentals ending tomorrow.
     */
    @Test
    void sendExpiryRemindersForTomorrowShouldSendReminder() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        when(dateTimeProvider.today()).thenReturn(today);

        Rental rental = createRental("R1", today.plusDays(1));
        rentalRepository.save(rental);

        int sentReminders = rentalReminderService.sendExpiryRemindersForTomorrow();

        assertEquals(1, sentReminders);
        verify(notificationService).sendRentalExpiryReminder(rental);
    }

    /**
     * Tests that no reminder is sent when rental does not end tomorrow.
     */
    @Test
    void sendExpiryRemindersForTomorrowShouldIgnoreOtherRentals() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        when(dateTimeProvider.today()).thenReturn(today);

        Rental rental = createRental("R1", today.plusDays(3));
        rentalRepository.save(rental);

        int sentReminders = rentalReminderService.sendExpiryRemindersForTomorrow();

        assertEquals(0, sentReminders);
        verifyNoInteractions(notificationService);
    }

    /**
     * Creates a rental for testing.
     *
     * @param rentalId the rental id
     * @param endDate  the rental end date
     * @return a rental
     */
    private Rental createRental(String rentalId, LocalDate endDate) {
        Customer customer = new Customer("C1", "Ahmad Ali", 25);
        Vehicle vehicle = new Vehicle("V1", "123-ABC", "Toyota", "Corolla", BigDecimal.valueOf(50));

        return new Rental(
                rentalId,
                customer,
                vehicle,
                LocalDate.of(2026, 1, 1),
                endDate
        );
    }
}