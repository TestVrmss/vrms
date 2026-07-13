package soft.eng.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class FileManagerRepositoryTest {

    @TempDir Path tempDir;


    @Test
    void loadsValidLoginFile() throws IOException {
        Path file = tempDir.resolve("login.txt");
        Files.writeString(file, "# comment\n\nmanager|secret\nadmin|pass\n");
        FileManagerRepository repository = new FileManagerRepository(file);
        assertEquals("manager", repository.findByUsername(" manager ").orElseThrow().getUsername());
        assertTrue(repository.findByUsername(null).isEmpty());
        assertTrue(repository.findByUsername("missing").isEmpty());
    }

    /** Invalid and missing files are rejected. */
    @Test
    void rejectsInvalidFiles() throws IOException {
        Path invalid = tempDir.resolve("invalid.txt");
        Files.writeString(invalid, "manager-only\n");
        assertThrows(IllegalStateException.class, () -> new FileManagerRepository(invalid));
        assertThrows(IllegalStateException.class,
                () -> new FileManagerRepository(tempDir.resolve("missing.txt")));
        assertThrows(NullPointerException.class, () -> new FileManagerRepository(null));
    }
}
