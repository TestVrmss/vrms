package soft.eng.persistence;

import java.util.Optional;

import soft.eng.domain.model.Manager;

/**
 * Defines manager data access operations.
 */
public interface ManagerRepository {

    /**
     * Finds a manager by username.
     *
     * @param username the username
     * @return an optional manager
     */
    Optional<Manager> findByUsername(String username);
}