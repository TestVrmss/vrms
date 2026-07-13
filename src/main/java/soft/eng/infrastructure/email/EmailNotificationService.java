package soft.eng.infrastructure.email;

import java.util.Objects;
import soft.eng.domain.model.Rental;
import soft.eng.infrastructure.NotificationService;

public final class EmailNotificationService implements NotificationService {
    private final MailGateway mailGateway;

    private final String subjectPrefix;

  
    public EmailNotificationService(MailGateway mailGateway, String subjectPrefix) {
        this.mailGateway = Objects.requireNonNull(mailGateway, "mailGateway must not be null");
        this.subjectPrefix = Objects.requireNonNull(subjectPrefix, "subjectPrefix must not be null").trim();
    }

    @Override
    public void onRentalExpiryReminder(Rental rental) {
        Objects.requireNonNull(rental, "rental must not be null");
        if (!mailGateway.isEnabled()) {
            return;
        }
        String subject = (subjectPrefix + " Rental expiry reminder").trim();
        String body = "Hello " + rental.getCustomer().getFullName() + ",\n\n"
                + "Your rental " + rental.getId() + " for vehicle "
                + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel()
                + " expires on " + rental.getEndDate() + ".\n"
                + "Please return the vehicle on time to avoid a late penalty.\n\n"
                + "Vehicle Rental Management System";
        mailGateway.send(rental.getCustomer().getEmail(), subject, body);
    }
}
