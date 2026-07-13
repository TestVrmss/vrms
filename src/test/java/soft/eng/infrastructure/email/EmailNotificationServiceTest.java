package soft.eng.infrastructure.email;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    @Mock private MailGateway mailGateway;
    private Rental rental;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer("C", "Rajaa", "rajaa@example.com", 30, false);
        Car car = new Car("V", "Toyota", "Corolla", new BigDecimal("40"));
        rental = new Rental("R1", customer, car,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12));
    }

    @Test
    void sendsReminderWhenEnabled() {
        when(mailGateway.isEnabled()).thenReturn(true);
        EmailNotificationService service = new EmailNotificationService(mailGateway, "[VRMS]");
        service.onRentalExpiryReminder(rental);
        verify(mailGateway).send(
                org.mockito.ArgumentMatchers.eq("rajaa@example.com"),
                contains("[VRMS] Rental expiry reminder"),
                contains("expires on 2026-07-12"));
    }

    /** Disabled gateway is skipped. */
    @Test
    void skipsReminderWhenDisabled() {
        when(mailGateway.isEnabled()).thenReturn(false);
        EmailNotificationService service = new EmailNotificationService(mailGateway, "[VRMS]");
        service.onRentalExpiryReminder(rental);
        verify(mailGateway, never()).send(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    /** Null constructor and event values are rejected. */
    @Test
    void rejectsNullValues() {
        assertThrows(NullPointerException.class, () -> new EmailNotificationService(null, "[VRMS]"));
        assertThrows(NullPointerException.class, () -> new EmailNotificationService(mailGateway, null));
        EmailNotificationService service = new EmailNotificationService(mailGateway, "[VRMS]");
        assertThrows(NullPointerException.class, () -> service.onRentalExpiryReminder(null));
    }
}
