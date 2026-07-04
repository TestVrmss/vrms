package soft.eng.domain.model;

import java.time.LocalDate;

import soft.eng.domain.enums.RentalStatus;

/**
 * Represents a rental record for a vehicle.
 */
public class Rental {

    /**
     * The rental unique identifier.
     */
    private final String id;

    /**
     * The customer who rents the vehicle.
     */
    private final Customer customer;

    /**
     * The rented vehicle.
     */
    private final Vehicle vehicle;

    /**
     * The rental start date.
     */
    private final LocalDate startDate;

    /**
     * The planned rental end date.
     */
    private final LocalDate endDate;

    /**
     * The actual return date.
     */
    private LocalDate returnDate;

    /**
     * The current rental status.
     */
    private RentalStatus status;

    /**
     * Creates a new active rental.
     *
     * @param id        the rental id
     * @param customer  the customer
     * @param vehicle   the vehicle
     * @param startDate the start date
     * @param endDate   the end date
     */
    public Rental(String id, Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = RentalStatus.ACTIVE;
    }

    /**
     * Gets the rental id.
     *
     * @return the rental id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets the rented vehicle.
     *
     * @return the vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Gets the rental start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the planned rental end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the actual return date.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Gets the rental status.
     *
     * @return the rental status
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Checks whether the rental is active.
     *
     * @return true if active, otherwise false
     */
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    /**
     * Closes the rental record.
     *
     * @param returnDate the actual return date
     */
    public void close(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = RentalStatus.CLOSED;
    }
}