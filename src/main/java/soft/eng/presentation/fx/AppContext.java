package soft.eng.presentation.fx;

import java.math.BigDecimal;
import java.nio.file.Path;

import soft.eng.application.AuthService;
import soft.eng.application.RentalReminderService;
import soft.eng.application.RentalService;
import soft.eng.application.VehicleCatalogService;
import soft.eng.domain.model.*;
import soft.eng.domain.strategy.DefaultRentalValidationStrategy;
import soft.eng.domain.strategy.StandardRentalPricingStrategy;
import soft.eng.infrastructure.SystemDateTimeProvider;
import soft.eng.infrastructure.UuidIdGenerator;
import soft.eng.infrastructure.email.EmailNotificationService;
import soft.eng.persistence.FileManagerRepository;
import soft.eng.persistence.InMemoryRentalRepository;
import soft.eng.persistence.InMemoryVehicleRepository;

public final class AppContext {

    private static final class Holder {
        private static final AppContext INSTANCE = new AppContext();
    }
    public static AppContext getInstance() { return Holder.INSTANCE; }

    private final AuthService            auth;
    private final VehicleCatalogService  catalog;
    private final RentalService          rental;
    private final RentalReminderService  reminder;
    private final EmailConfigService     emailCfg;
    private EmailNotificationService     emailObserver;

    private AppContext() {
        var loginPath = Path.of(System.getProperty("vrms.login.file", "config/login.txt"));
        auth = new AuthService(new FileManagerRepository(loginPath));

        var vehicles = new InMemoryVehicleRepository();
        var rentals  = new InMemoryRentalRepository();
        seedVehicles(vehicles);

        var clock = new SystemDateTimeProvider();
        catalog  = new VehicleCatalogService(auth, vehicles);
        rental   = new RentalService(auth, rentals, vehicles,
                     new DefaultRentalValidationStrategy(),
                     new StandardRentalPricingStrategy(), clock, new UuidIdGenerator());
        reminder = new RentalReminderService(rentals, clock);
        emailCfg = new EmailConfigService();
        registerEmail();
    }

    public AuthService           auth()     { return auth; }
    public VehicleCatalogService catalog()  { return catalog; }
    public RentalService         rental()   { return rental; }
    public RentalReminderService reminder() { return reminder; }
    public EmailConfigService    emailCfg() { return emailCfg; }

    public void reloadEmail() {
        if (emailObserver != null) reminder.removeObserver(emailObserver);
        registerEmail();
    }

    private void registerEmail() {
        try {
            if (emailCfg.isEnabled()
                    && !emailCfg.getHost().isBlank()
                    && !emailCfg.getUsername().isBlank()
                    && !emailCfg.getPassword().isBlank()) {
                emailObserver = new EmailNotificationService(
                    emailCfg.buildGateway(), emailCfg.getSubjectPrefix());
                reminder.registerObserver(emailObserver);
            } else {
                // Email not configured yet — disable silently
                emailCfg.setEnabled(false);
                emailObserver = null;
            }
        } catch (Exception e) {
            System.err.println("[VRMS] Email init skipped: " + e.getMessage());
            emailCfg.setEnabled(false);
            emailObserver = null;
        }
    }

    private static void seedVehicles(InMemoryVehicleRepository r) {
        Vehicle[] v = {
            new Car("CAR-001",  "Toyota",   "Corolla",  new BigDecimal("40.00")),
            new Car("CAR-002",  "Honda",    "Civic",    new BigDecimal("45.00")),
            new Car("CAR-003",  "BMW",      "320i",     new BigDecimal("85.00")),
            new Motorcycle("MOTO-001", "Honda",  "CB500F",  new BigDecimal("25.00")),
            new Motorcycle("MOTO-002", "Yamaha", "MT-07",   new BigDecimal("30.00")),
            new Van("VAN-001",  "Ford",     "Transit",  new BigDecimal("65.00")),
            new Van("VAN-002",  "Mercedes", "Sprinter", new BigDecimal("80.00")),
            new Truck("TRK-001","Volvo",    "FM",       new BigDecimal("120.00")),
            new ElectricVehicle("EV-001", "Tesla",   "Model 3", new BigDecimal("80.00"), 90),
            new ElectricVehicle("EV-002", "BMW",     "iX",      new BigDecimal("95.00"), 75),
            new ElectricVehicle("EV-003", "Polestar","2",       new BigDecimal("70.00"), 80),
        };
        for (var veh : v) r.save(veh);
    }
}