package soft.eng.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import soft.eng.domain.enums.RentalStatus;


public final class Rental {

    private final String id;

    private final Customer customer;

    private final Vehicle vehicle;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private RentalStatus status;

    private LocalDate actualReturnDate;

    
    public Rental(String id, Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.id = id.trim();
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle must not be null");
        this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
        this.endDate = Objects.requireNonNull(endDate, "endDate must not be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        this.status = RentalStatus.ACTIVE;
    }


    public String getId() { return id; }

    public Customer getCustomer() { return customer; }

    public Vehicle getVehicle() { return vehicle; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public RentalStatus getStatus() { return status; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }

    public boolean isActive() { return status == RentalStatus.ACTIVE; }

    
    
    public long getRentalDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    
    
    public long getLateDays(LocalDate returnDate) {
        Objects.requireNonNull(returnDate, "returnDate must not be null");
        return Math.max(0, ChronoUnit.DAYS.between(endDate, returnDate));
    }

    
    
    public boolean expiresWithin(LocalDate today, int daysAhead) {
        Objects.requireNonNull(today, "today must not be null");
        if (daysAhead < 0) {
            throw new IllegalArgumentException("daysAhead must not be negative");
        }
        return isActive() && !endDate.isBefore(today) && !endDate.isAfter(today.plusDays(daysAhead));
    }

    
    public void close(LocalDate returnDate) {
        if (!isActive()) {
            throw new IllegalStateException("only an active rental can be closed");
        }
        Objects.requireNonNull(returnDate, "returnDate must not be null");
        if (returnDate.isBefore(startDate)) {
            throw new IllegalArgumentException("returnDate must not be before startDate");
        }
        actualReturnDate = returnDate;
        status = RentalStatus.COMPLETED;
    }

    public void cancel() {
        if (!isActive()) {
            throw new IllegalStateException("only an active rental can be cancelled");
        }
        status = RentalStatus.CANCELLED;
    }

   
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Rental rental)) return false;
        return id.equals(rental.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Rental{id='" + id + "', customer=" + customer.getFullName() + ", vehicle=" + vehicle.getId()
                + ", start=" + startDate + ", end=" + endDate + ", status=" + status + "}";
    }
}
