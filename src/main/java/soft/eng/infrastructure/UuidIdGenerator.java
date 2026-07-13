package soft.eng.infrastructure;

import java.util.UUID;


public final class UuidIdGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
