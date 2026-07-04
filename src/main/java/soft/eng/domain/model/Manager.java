package soft.eng.domain.model;

/**
 * Represents a system manager who can log in and perform protected actions.
 */
public class Manager {

    /**
     * The manager username.
     */
    private final String username;

    /**
     * The manager password.
     */
    private final String password;

    /**
     * Creates a new manager.
     *
     * @param username the manager username
     * @param password the manager password
     */
    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the manager username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Checks whether the given password matches this manager password.
     *
     * @param password the password to check
     * @return true if the password matches, otherwise false
     */
    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }
}