package soft.eng.domain.model;

/**
 * Represents a customer who rents vehicles.
 */
public class Customer {

    /**
     * The customer unique identifier.
     */
    private final String id;

    /**
     * The customer full name.
     */
    private final String name;

    /**
     * The customer age.
     */
    private final int age;

    /**
     * Creates a new customer.
     *
     * @param id   the customer id
     * @param name the customer name
     * @param age  the customer age
     */
    public Customer(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    /**
     * Gets the customer id.
     *
     * @return the customer id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the customer name.
     *
     * @return the customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the customer age.
     *
     * @return the customer age
     */
    public int getAge() {
        return age;
    }
}