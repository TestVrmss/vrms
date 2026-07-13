package soft.eng.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import soft.eng.domain.model.Car;
import soft.eng.domain.model.Customer;
import soft.eng.domain.model.Manager;
import soft.eng.domain.model.Rental;
import soft.eng.domain.model.Vehicle;

class InMemoryRepositoriesTest {
    @Test
    void managerRepositoryWorks() {
        Manager first = new Manager("a", "1");
        InMemoryManagerRepository repository = new InMemoryManagerRepository(List.of(first));
        assertEquals(first, repository.findByUsername(" a ").orElseThrow());
        assertTrue(repository.findByUsername(null).isEmpty());
        assertTrue(repository.findByUsername("missing").isEmpty());
        Manager second = new Manager("b", "2");
        repository.save(second);
        assertEquals(second, repository.findByUsername("b").orElseThrow());
        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertThrows(NullPointerException.class, () -> new InMemoryManagerRepository(null));
        assertTrue(new InMemoryManagerRepository().findByUsername("x").isEmpty());
    }

    /** Vehicle repository filters availability and supports replacement. */
    @Test
    void vehicleRepositoryWorks() {
        InMemoryVehicleRepository repository = new InMemoryVehicleRepository();
        Vehicle available = new Car("A", "Toyota", "Yaris", new BigDecimal("30"));
        Vehicle rented = new Car("R", "Honda", "Civic", new BigDecimal("35"));
        rented.rent();
        repository.save(available);
        repository.save(rented);
        assertEquals(2, repository.findAll().size());
        assertEquals(List.of(available), repository.findAvailable());
        assertEquals(available, repository.findById(" A ").orElseThrow());
        assertTrue(repository.findById(null).isEmpty());
        assertTrue(repository.findById("missing").isEmpty());
        Vehicle replacement = new Car("A", "New", "Model", new BigDecimal("99"));
        repository.save(replacement);
        assertEquals(replacement, repository.findById("A").orElseThrow());
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    /** Rental repository filters active rentals and prevents active duplicates. */
    @Test
    void rentalRepositoryWorks() {
        InMemoryRentalRepository repository = new InMemoryRentalRepository();
        Customer customer = new Customer("C", "Customer", "c@example.com", 30, false);
        Vehicle vehicle = new Car("V", "Toyota", "Yaris", new BigDecimal("30"));
        Rental active = new Rental("R1", customer, vehicle, LocalDate.now(), LocalDate.now());
        Rental completed = new Rental("R2", customer,
                new Car("V2", "Ford", "Focus", new BigDecimal("30")), LocalDate.now(), LocalDate.now());
        completed.close(LocalDate.now());
        repository.save(active);
        repository.save(completed);
        assertEquals(2, repository.findAll().size());
        assertEquals(List.of(active), repository.findActive());
        assertEquals(active, repository.findById(" R1 ").orElseThrow());
        assertTrue(repository.findById(null).isEmpty());
        assertTrue(repository.findById("missing").isEmpty());
        assertTrue(repository.existsActiveRentalForVehicle("V"));
        assertFalse(repository.existsActiveRentalForVehicle("V2"));
        assertFalse(repository.existsActiveRentalForVehicle(null));
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}
