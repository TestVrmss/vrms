package soft.eng.persistence;

import java.util.Optional;

import soft.eng.domain.model.Manager;


public interface ManagerRepository {

    
    Optional<Manager> findByUsername(String username);
}