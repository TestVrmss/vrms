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

  
    public void logout() {
        loggedInManager = null;
    }

    
    public boolean isLoggedIn() {
        return loggedInManager != null;
    }

   
    public void requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Manager must login first.");
        }
    }
}