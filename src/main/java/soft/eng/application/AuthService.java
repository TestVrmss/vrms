package soft.eng.application;

import java.util.Objects;
import java.util.Optional;
import soft.eng.domain.model.Manager;
import soft.eng.persistence.ManagerRepository;


public final class AuthService {
   
    private final ManagerRepository managerRepository;
    private Manager currentManager;

   
    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository = Objects.requireNonNull(managerRepository, "managerRepository must not be null");
    }

    
    public boolean login(String username, String password) {
        currentManager = null;
        if (username == null || password == null) {
            return false;
        }
        Optional<Manager> manager = managerRepository.findByUsername(username.trim());
        if (manager.isPresent() && manager.get().passwordMatches(password)) {
            currentManager = manager.get();
            return true;
        }
        return false;
    }

   
    public void logout() {
        currentManager = null;
    }

   
    public boolean isLoggedIn() {
        return currentManager != null;
    }

    public Optional<Manager> getCurrentManager() {
        return Optional.ofNullable(currentManager);
    }

    
    public void requireAuthenticated() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("manager login is required");
        }
    }
}
