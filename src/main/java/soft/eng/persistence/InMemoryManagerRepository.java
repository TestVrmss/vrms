package soft.eng.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Manager;

/**
 * In-memory implementation of manager repository.
 */
public class InMemoryManagerRepository implements ManagerRepository {

    /**
     * Stores managers by username.
     */
    private final Map<String, Manager> managers = new HashMap<>();

    /**
     * Creates a repository with one default manager.
     */
    public InMemoryManagerRepository() {
        save(new Manager("admin", "admin123"));
    }

    /**
     * Saves a manager.
     *
     * @param manager the manager to save
     */
    public void save(Manager manager) {
        managers.put(manager.getUsername(), manager);
    }

    /**
     * Finds a manager by username.
     *
     * @param username the username
     * @return an optional manager
     */
    @Override
    public Optional<Manager> findByUsername(String username) {
        return Optional.ofNullable(managers.get(username));
    }
}