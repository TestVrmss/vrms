package soft.eng.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import soft.eng.domain.model.Manager;


public final class FileManagerRepository implements ManagerRepository {
    private final Map<String, Manager> managers;

    
    public FileManagerRepository(Path loginFile) {
        Objects.requireNonNull(loginFile, "loginFile must not be null");
        this.managers = load(loginFile);
    }

    @Override
    public Optional<Manager> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(managers.get(username.trim()));
    }

    /** Reads and parses the file. */
    private static Map<String, Manager> load(Path loginFile) {
        if (!Files.isRegularFile(loginFile)) {
            throw new IllegalStateException("Login file not found: " + loginFile);
        }
        try {
            List<String> lines = Files.readAllLines(loginFile, StandardCharsets.UTF_8);
            Map<String, Manager> result = new LinkedHashMap<>();
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index).trim();
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalStateException("Invalid login row at line " + (index + 1));
                }
                Manager manager = new Manager(parts[0], parts[1]);
                result.put(manager.getUsername(), manager);
            }
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read login file: " + loginFile, exception);
        }
    }
}
