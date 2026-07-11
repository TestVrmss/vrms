package soft.eng.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import soft.eng.domain.model.Manager;


public class InMemoryManagerRepository implements ManagerRepository {

    private final Map<String, Manager> managers = new HashMap<>();

 
    public InMemoryManagerRepository() {
        save(new Manager("admin", "admin123"));
    }

   
    public void save(Manager manager) {
        managers.put(manager.getUsername(), manager);
    }

   
    @Override
    public Optional<Manager> findByUsername(String username) {
        return Optional.ofNullable(managers.get(username));
    }
}