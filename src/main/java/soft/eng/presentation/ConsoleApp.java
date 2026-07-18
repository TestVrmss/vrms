package soft.eng.presentation;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import soft.eng.application.AuthService;
import soft.eng.application.RentalReminderService;
import soft.eng.application.RentalService;
import soft.eng.application.VehicleCatalogService;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.ElectricVehicle;
import soft.eng.domain.model.Motorcycle;
import soft.eng.domain.model.Truck;
import soft.eng.domain.model.Van;
import soft.eng.domain.model.Vehicle;
import soft.eng.domain.strategy.DefaultRentalValidationStrategy;
import soft.eng.domain.strategy.StandardRentalPricingStrategy;
import soft.eng.infrastructure.SystemDateTimeProvider;
import soft.eng.infrastructure.UuidIdGenerator;
import soft.eng.infrastructure.config.ApplicationConfig;
import soft.eng.infrastructure.config.EmailSettings;
import soft.eng.infrastructure.email.EmailNotificationService;
import soft.eng.infrastructure.email.JavaMailGateway;
import soft.eng.persistence.FileManagerRepository;
import soft.eng.persistence.InMemoryRentalRepository;
import soft.eng.persistence.InMemoryVehicleRepository;

public final class ConsoleApp {

    private final Scanner scanner;
    private final AuthService authService;
    private final VehicleCatalogService catalogService;
    private final RentalService rentalService;
    private final RentalReminderService reminderService;

    public ConsoleApp(Scanner scanner, AuthService authService,
                      VehicleCatalogService catalogService,
                      RentalService rentalService,
                      RentalReminderService reminderService) {
        this.scanner        = scanner;
        this.authService    = authService;
        this.catalogService = catalogService;
        this.rentalService  = rentalService;
        this.reminderService = reminderService;
    }

    public static void main(String[] args) {
        Path loginPath = Path.of(System.getProperty("vrms.login.file", "config/login.txt"));
        AuthService auth = new AuthService(new FileManagerRepository(loginPath));

        InMemoryVehicleRepository vehicles = new InMemoryVehicleRepository();
        InMemoryRentalRepository  rentals  = new InMemoryRentalRepository();
        seedVehicles(vehicles);

        SystemDateTimeProvider clock = new SystemDateTimeProvider();
        VehicleCatalogService catalog = new VehicleCatalogService(auth, vehicles);
        RentalService rentalService = new RentalService(
                auth, rentals, vehicles,
                new DefaultRentalValidationStrategy(),
                new StandardRentalPricingStrategy(), clock, new UuidIdGenerator());
        RentalReminderService reminderService = new RentalReminderService(rentals, clock);

        // ✅ Fix: wrap email init in try-catch so ConsoleApp works even without email config
        try {
            ApplicationConfig config = ApplicationConfig.getInstance();
            EmailSettings emailSettings = EmailSettings.from(config);
            reminderService.registerObserver(new EmailNotificationService(
                    new JavaMailGateway(emailSettings), emailSettings.getSubjectPrefix()));
            System.out.println("[Email] Notifications enabled.");
        } catch (Exception e) {
            System.out.println("[Email] Notifications disabled: " + e.getMessage());
        }

        new ConsoleApp(new Scanner(System.in), auth, catalog, rentalService, reminderService).run();
    }

    /** Runs the interactive menu until the user exits. */
    public void run() {
        boolean running = true;
        System.out.println("Vehicle Rental Management System");
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> login();
                    case "2" -> logout();
                    case "3" -> showAvailableVehicles();
                    case "4" -> rentVehicle();
                    case "5" -> returnVehicle();
                    case "6" -> sendReminders();
                    case "7" -> showActiveRentals();
                    case "0" -> running = false;
                    default  -> System.out.println("Unknown option.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    /** Prints the application menu. */
    private void printMenu() {
        System.out.println("\n1 Login | 2 Logout | 3 Available vehicles | 4 Rent | 5 Return"
                + " | 6 Send reminders | 7 Active rentals | 0 Exit");
        System.out.print("> ");
    }

    /** Handles manager login. */
    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println(authService.login(username, password)
                ? "Login successful." : "Invalid credentials.");
    }

    /** Handles manager logout. */
    private void logout() {
        authService.logout();
        System.out.println("Logged out.");
    }

    /** Displays available vehicles. */
    private void showAvailableVehicles() {
        catalogService.getAvailableVehicles().forEach(System.out::println);
    }

    /** Collects rental input and creates a rental. */
    private void rentVehicle() {
        System.out.print("Customer id: ");       String customerId     = scanner.nextLine();
        System.out.print("Customer full name: "); String fullName       = scanner.nextLine();
        System.out.print("Customer email: ");     String email          = scanner.nextLine();
        System.out.print("Customer age: ");       int age               = Integer.parseInt(scanner.nextLine());
        System.out.print("Special truck license (yes/no): ");
        boolean specialLicense = scanner.nextLine().trim().equalsIgnoreCase("yes");
        System.out.print("Vehicle id: ");         String vehicleId      = scanner.nextLine();
        System.out.print("Start date (YYYY-MM-DD): "); LocalDate start  = LocalDate.parse(scanner.nextLine());
        System.out.print("End date (YYYY-MM-DD): ");   LocalDate end    = LocalDate.parse(scanner.nextLine());

        Customer customer = new Customer(customerId, fullName, email, age, specialLicense);
        var rental = rentalService.rentVehicle(customer, vehicleId, start, end);
        System.out.println("Rental created: " + rental.getId());
    }

    /** Processes a vehicle return. */
    private void returnVehicle() {
        System.out.print("Rental id: ");
        String rentalId = scanner.nextLine();
        BigDecimal total = rentalService.returnVehicle(rentalId);
        System.out.println("Rental closed. Total cost: " + total);
    }

    /** Sends reminders for rentals ending within two days. */
    private void sendReminders() {
        int notifications = reminderService.checkExpiringRentals(2);
        System.out.println("Generated notifications: " + notifications);
    }

    /** Displays active rentals. */
    private void showActiveRentals() {
        rentalService.getActiveRentals().forEach(System.out::println);
    }

    /** Seeds sample vehicles for the console demo. */
    private static void seedVehicles(InMemoryVehicleRepository repository) {
        Vehicle[] sampleVehicles = {
            new Car("CAR-1",    "Toyota", "Corolla",  new BigDecimal("40.00")),
            new Motorcycle("MOTO-1", "Honda",  "CB500",   new BigDecimal("25.00")),
            new Van("VAN-1",    "Ford",   "Transit",  new BigDecimal("65.00")),
            new Truck("TRUCK-1","Volvo",  "FM",       new BigDecimal("120.00")),
            new ElectricVehicle("EV-1", "Tesla", "Model 3", new BigDecimal("80.00"), 90)
        };
        for (Vehicle vehicle : sampleVehicles) {
            repository.save(vehicle);
        }
    }
}