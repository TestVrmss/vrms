package soft.eng.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import soft.eng.infrastructure.config.ApplicationConfig;


class InfrastructureUtilitiesTest {

    @Test
    void productionUtilitiesWork() {
        assertNotNull(new SystemDateTimeProvider().today());
        UuidIdGenerator generator = new UuidIdGenerator();
        String first = generator.nextId();
        String second = generator.nextId();
        assertFalse(first.isBlank());
        assertNotEquals(first, second);
    }


    @Test
    void applicationConfigIsSingleton() {
        ApplicationConfig first = ApplicationConfig.getInstance();
        ApplicationConfig second = ApplicationConfig.getInstance();
        assertSame(first, second);
        assertNotNull(first.getString("mail.smtp.host", "fallback"));
        first.getBoolean("mail.enabled", false);
        first.getInt("mail.smtp.port", 587);
        first.getString("missing.property", "fallback");
    }
}
