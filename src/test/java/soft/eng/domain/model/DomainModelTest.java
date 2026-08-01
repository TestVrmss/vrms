package soft.eng.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import soft.eng.domain.enums.RentalStatus;
import soft.eng.domain.enums.VehicleStatus;
import soft.eng.domain.enums.VehicleType;

class DomainModelTest {
    @Test
    void managerBehavior() {
        Manager first = new Manager(" manager ", "secret");
        Manager same = new Manager("manager", "other");
        Manager different = new Manager("other", "secret");
        assertEquals("manager", first.getUsername());
        assertTrue(first.passwordMatches("secret"));
        assertFalse(first.passwordMatches("bad"));
        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("manager"));
        assertThrows(IllegalArgumentException.class, () -> new Manager(" ", "x"));
        assertThrows(IllegalArgumentException.class, () -> new Manager("x", null));
    }

    @Test
    void customerBehavior() {
        Customer customer = new Customer(" C1 ", " Rajaa ", "r@example.com", 25, true);
        Customer same = new Customer("C1", "Other", "o@example.com", 30, false);
        Customer different = new Customer("C2", "Other", "o@example.com", 30, false);
        assertEquals("C1", customer.getId());
        assertEquals("Rajaa", customer.getFullName());
        assertEquals("r@example.com", customer.getEmail());
        assertEquals(25, customer.getAge());
        assertTrue(customer.hasSpecialLicense());
        assertEquals(customer, same);
        assertEquals(customer.hashCode(), same.hashCode());
        assertNotEquals(customer, different);
        assertTrue(customer.toString().contains("r@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new Customer("", "N", "a@b.com", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new Customer("1", "", "a@b.com", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new Customer("1", "N", "bad", 1, false));
        assertThrows(IllegalArgumentException.class, () -> new Customer("1", "N", "a@b.com", -1, false));
        assertThrows(IllegalArgumentException.class, () -> new Customer("1", "N", "a@b.com", 121, false));
    }

    @Test
    void vehicleTypesAndStates() {
        Vehicle car = new Car("C", "Toyota", "Corolla", new BigDecimal("40"));
        Vehicle motorcycle = new Motorcycle("M", "Honda", "CB", new BigDecimal("20"));
        Vehicle van = new Van("V", "Ford", "Transit", new BigDecimal("60"));
        Vehicle truck = new Truck("T", "Volvo", "FM", new BigDecimal("100"));
        ElectricVehicle electric = new ElectricVehicle("E", "Tesla", "3", new BigDecimal("80"), 90);

        assertEquals(VehicleType.CAR, car.getType());
        assertEquals(VehicleType.MOTORCYCLE, motorcycle.getType());
        assertEquals(VehicleType.VAN, van.getType());
        assertEquals(VehicleType.TRUCK, truck.getType());
        assertEquals(VehicleType.ELECTRIC, electric.getType());
        assertEquals("Toyota", car.getBrand());
        assertEquals("Corolla", car.getModel());
        assertEquals(new BigDecimal("40"), car.getDailyRate());
        assertTrue(car.isAvailable());
        car.rent();
        assertEquals(VehicleStatus.RENTED, car.getStatus());
        assertFalse(car.isAvailable());
        assertThrows(IllegalStateException.class, car::rent);
        car.makeAvailable();
        assertTrue(car.isAvailable());
        car.sendToMaintenance();
        assertEquals(VehicleStatus.MAINTENANCE, car.getStatus());
        assertTrue(car.toString().contains("Toyota"));

        assertEquals(90, electric.getBatteryLevel());
        electric.updateBatteryLevel(55);
        assertEquals(55, electric.getBatteryLevel());
        assertThrows(IllegalArgumentException.class, () -> electric.updateBatteryLevel(-1));
        assertThrows(IllegalArgumentException.class, () -> electric.updateBatteryLevel(101));

        assertEquals(car, new Car("C", "Other", "Other", BigDecimal.ONE));
        assertEquals(car.hashCode(), new Car("C", "Other", "Other", BigDecimal.ONE).hashCode());
        assertNotEquals(car, motorcycle);
        assertThrows(IllegalArgumentException.class, () -> new Car("", "B", "M", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Car("1", "", "M", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Car("1", "B", "", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Car("1", "B", "M", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new ElectricVehicle("E2", "B", "M", BigDecimal.ONE, -1));
    }

    @Test
    void rentalLifecycle() {
        Customer customer = new Customer("1", "Customer", "c@example.com", 30, false);
        Vehicle vehicle = new Car("V", "Toyota", "Yaris", new BigDecimal("30"));
        LocalDate start = LocalDate.of(2026, 7, 10);
        LocalDate end = LocalDate.of(2026, 7, 12);
        Rental rental = new Rental("R", customer, vehicle, start, end);

        assertEquals("R", rental.getId());
        assertEquals(customer, rental.getCustomer());
        assertEquals(vehicle, rental.getVehicle());
        assertEquals(start, rental.getStartDate());
        assertEquals(end, rental.getEndDate());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertNull(rental.getActualReturnDate());
        assertTrue(rental.isActive());
        assertEquals(3, rental.getRentalDays());
        assertEquals(0, rental.getLateDays(end));
        assertEquals(2, rental.getLateDays(end.plusDays(2)));
        assertTrue(rental.expiresWithin(start, 2));
        assertFalse(rental.expiresWithin(start.minusDays(4), 2));
        assertFalse(rental.expiresWithin(end.plusDays(1), 3));
        assertThrows(IllegalArgumentException.class, () -> rental.expiresWithin(start, -1));
        assertThrows(NullPointerException.class, () -> rental.expiresWithin(null, 1));
        assertThrows(NullPointerException.class, () -> rental.getLateDays(null));

        rental.close(end.plusDays(1));
        assertEquals(RentalStatus.COMPLETED, rental.getStatus());
        assertEquals(end.plusDays(1), rental.getActualReturnDate());
        assertFalse(rental.isActive());
        assertThrows(IllegalStateException.class, () -> rental.close(end));
        assertThrows(IllegalStateException.class, rental::cancel);
        assertTrue(rental.toString().contains("R"));

        Rental cancellable = new Rental("R2", customer, vehicle, start, end);
        cancellable.cancel();
        assertEquals(RentalStatus.CANCELLED, cancellable.getStatus());
        assertThrows(IllegalStateException.class, cancellable::cancel);
        assertEquals(cancellable, new Rental("R2", customer, vehicle, start, end));
        assertEquals(cancellable.hashCode(), new Rental("R2", customer, vehicle, start, end).hashCode());
        assertNotEquals(cancellable, rental);
   

        assertThrows(IllegalArgumentException.class, () -> new Rental("", customer, vehicle, start, end));
        assertThrows(NullPointerException.class, () -> new Rental("X", null, vehicle, start, end));
        assertThrows(NullPointerException.class, () -> new Rental("X", customer, null, start, end));
        assertThrows(NullPointerException.class, () -> new Rental("X", customer, vehicle, null, end));
        assertThrows(NullPointerException.class, () -> new Rental("X", customer, vehicle, start, null));
        assertThrows(IllegalArgumentException.class, () -> new Rental("X", customer, vehicle, end, start));
        Rental tooEarly = new Rental("R3", customer, vehicle, start, end);
        assertThrows(IllegalArgumentException.class, () -> tooEarly.close(start.minusDays(1)));
        assertThrows(NullPointerException.class, () -> tooEarly.close(null));
    }
}
