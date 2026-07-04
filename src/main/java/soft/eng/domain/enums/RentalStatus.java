package soft.eng.domain.enums;

/**
 * Represents the current status of a rental record.
 */
public enum RentalStatus {

    /**
     * The rental is active and the vehicle is still rented.
     */
    ACTIVE,

    /**
     * The rental is closed after the vehicle has been returned.
     */
    CLOSED
}