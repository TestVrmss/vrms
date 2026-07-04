package soft.eng.presentation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import soft.eng.application.AuthService;
import soft.eng.application.RentalService;
import soft.eng.application.VehicleCatalogService;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;
import soft.eng.persistence.InMemoryManagerRepository;
import soft.eng.persistence.InMemoryRentalRepository;
import soft.eng.persistence.InMemoryVehicleRepository;

/**
 * A simple console application to demonstrate Phase 1 features.
 */
public class ConsoleApp {

    /**
     * Runs a simple demonstration of the vehicle rental system.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        InMemoryManagerRepository managerRepository = new InMemoryManagerRepository();
        InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
        InMemoryRentalRepository rentalRepository = new InMemoryRentalRepository();

        AuthService authService = new AuthService(managerRepository);
        VehicleCatalogService vehicleCatalogService = new VehicleCatalogService(authService, vehicleRepository);
        RentalService rentalService = new RentalService(authService, vehicleRepository, rentalRepository);

        Vehicle vehicleOne = new Vehicle("V1", "111-A", "Toyota", "Corolla", BigDecimal.valueOf(50));
        Vehicle vehicleTwo = new Vehicle("V2", "222-B", "Honda", "Civic", BigDecimal.valueOf(60));

        vehicleRepository.save(vehicleOne);
        vehicleRepository.save(vehicleTwo);

        System.out.println("Vehicle Rental Management System - Phase 1");
        System.out.println("------------------------------------------");

        boolean loginSuccess = authService.login("admin", "admin123");
        System.out.println("Login success: " + loginSuccess);

        System.out.println();
        System.out.println("Available vehicles before rental:");

        List<Vehicle> availableVehicles = vehicleCatalogService.getAvailableVehicles();

        for (Vehicle vehicle : availableVehicles) {
            System.out.println(vehicle.getId() + " - "
                    + vehicle.getBrand() + " "
                    + vehicle.getModel() + " - "
                    + vehicle.getStatus());
        }

        Customer customer = new Customer("C1", "Ahmad Ali", 25);

        Rental rental = rentalService.rentVehicle(
                "V1",
                customer,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 5)
        );

        System.out.println();
        System.out.println("Rental created successfully:");
        System.out.println("Rental ID: " + rental.getId());
        System.out.println("Customer: " + rental.getCustomer().getName());
        System.out.println("Vehicle: " + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel());
        System.out.println("Vehicle status after rental: " + rental.getVehicle().getStatus());

        System.out.println();
        System.out.println("Available vehicles after rental:");

        List<Vehicle> availableVehiclesAfterRental = vehicleCatalogService.getAvailableVehicles();

        for (Vehicle vehicle : availableVehiclesAfterRental) {
            System.out.println(vehicle.getId() + " - "
                    + vehicle.getBrand() + " "
                    + vehicle.getModel() + " - "
                    + vehicle.getStatus());
        }

        authService.logout();

        System.out.println();
        System.out.println("Manager logged out.");
        System.out.println("Is manager logged in? " + authService.isLoggedIn());
    }
}