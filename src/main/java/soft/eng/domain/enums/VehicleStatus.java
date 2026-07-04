package soft.eng.domain.enums;

/**
 * Represents the current availability status of a vehicle.
 */
public enum VehicleStatus {

    /**
     * The vehicle is available and can be rented.
     */
    AVAILABLE,

    /**
     * The vehicle is currently rented and cannot be rented again.
     */
    RENTED
}