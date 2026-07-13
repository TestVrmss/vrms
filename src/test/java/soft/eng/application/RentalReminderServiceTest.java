package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.infrastructure.DateTimeProvider;
import soft.eng.infrastructure.NotificationService;
import soft.eng.persistence.RentalRepository;

@ExtendWith(MockitoExtension.class)
class RentalReminderServiceTest {

    @Mock private RentalRepository rentalRepository;

    @Mock private DateTimeProvider dateTimeProvider;

    @Mock private NotificationService firstObserver;

    @Mock private NotificationService secondObserver;

    private RentalReminderService service;

    private LocalDate today;


    @BeforeEach
    void setUp() {
        service = new RentalReminderService(rentalRepository, dateTimeProvider);
        today = LocalDate.of(2026, 7, 11);
    }


    @Test
    void managesObservers() {
        service.registerObserver(firstObserver);
        service.registerObserver(firstObserver);
        service.registerObserver(secondObserver);
        assertEquals(2, service.observerCount());
        assertTrue(service.removeObserver(firstObserver));
        assertFalse(service.removeObserver(firstObserver));
        assertEquals(1, service.observerCount());
        assertThrows(NullPointerException.class, () -> service.registerObserver(null));
    }


    @Test
    void notifiesObserversForExpiringRentals() {
        Rental expiring = rental("R1", today.plusDays(2));
        Rental later = rental("R2", today.plusDays(5));
        when(dateTimeProvider.today()).thenReturn(today);
        when(rentalRepository.findActive()).thenReturn(List.of(expiring, later));
        service.registerObserver(firstObserver);
        service.registerObserver(secondObserver);

        assertEquals(2, service.checkExpiringRentals(2));
        verify(firstObserver).onRentalExpiryReminder(expiring);
        verify(secondObserver).onRentalExpiryReminder(expiring);
        verify(firstObserver, never()).onRentalExpiryReminder(later);
    }


    @Test
    void handlesNoObserversAndRejectsNegativeWindow() {
        when(dateTimeProvider.today()).thenReturn(today);
        when(rentalRepository.findActive()).thenReturn(List.of(rental("R1", today)));
        assertEquals(0, service.checkExpiringRentals(0));
        assertThrows(IllegalArgumentException.class, () -> service.checkExpiringRentals(-1));
    }


    @Test
    void constructorRejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new RentalReminderService(null, dateTimeProvider));
        assertThrows(NullPointerException.class, () -> new RentalReminderService(rentalRepository, null));
    }


    private static Rental rental(String id, LocalDate endDate) {
        Customer customer = new Customer("C" + id, "Customer", id.toLowerCase() + "@example.com", 30, false);
        Car vehicle = new Car("V" + id, "Toyota", "Yaris", new BigDecimal("30"));
        return new Rental(id, customer, vehicle, endDate.minusDays(1), endDate);
    }
}
