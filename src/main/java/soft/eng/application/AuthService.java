package soft.eng.application;

import java.util.Optional;

import soft.eng.domain.model.Manager;
import soft.eng.persistence.ManagerRepository;


public class AuthService {

    
    private final ManagerRepository managerRepository;

    
    private Manager loggedInManager;

    
    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    
    public boolean login(String username, String password) {
        Optional<Manager> manager = managerRepository.findByUsername(username);

        if (manager.isPresent() && manager.get().hasPassword(password)) {
            loggedInManager = manager.get();
            return true;
        }

        return false;
    }

    /**
     * Logs out the current manager.
     */
    public void logout() {
        loggedInManager = null;
    }

    /**
     * Checks whether a manager is logged in.
     *
     * @return true if logged in, otherwise false
     */
    public boolean isLoggedIn() {
        return loggedInManager != null;
    }

    /**
     * Requires a manager to be logged in before performing a protected action.
     */
    public void requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Manager must login first.");
        }
    }
}