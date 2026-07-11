package soft.eng.domain.model;

import java.time.LocalDate;

import soft.eng.domain.enums.RentalStatus;


public class Rental {

    
    private final String id;

   
    private final Customer customer;

   
    private final Vehicle vehicle;

    private final LocalDate startDate;

    
    private final LocalDate endDate;

    
    private LocalDate returnDate;

   
    private RentalStatus status;

    
    public Rental(String id, Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = RentalStatus.ACTIVE;
    }

    
    public String getId() {
        return id;
    }

   
    public Customer getCustomer() {
        return customer;
    }

    
    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public LocalDate getReturnDate() {
        return returnDate;
    }

   
    public RentalStatus getStatus() {
        return status;
    }

    
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    
    public void close(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = RentalStatus.CLOSED;
    }
}