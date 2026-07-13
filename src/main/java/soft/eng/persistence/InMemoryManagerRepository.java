package soft.eng.persistence;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import soft.eng.domain.model.Manager;


public final class InMemoryManagerRepository implements ManagerRepository {
    private final Map<String, Manager> managers = new LinkedHashMap<>();

    public InMemoryManagerRepository() {
    }

    public InMemoryManagerRepository(Collection<Manager> initialManagers) {
        Objects.requireNonNull(initialManagers, "initialManagers must not be null");
        initialManagers.forEach(this::save);
    }

 
    public void save(Manager manager) {
        Manager validManager = Objects.requireNonNull(manager, "manager must not be null");
        managers.put(validManager.getUsername(), validManager);
    }

    @Override
    public Optional<Manager> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(managers.get(username.trim()));
    }
}
