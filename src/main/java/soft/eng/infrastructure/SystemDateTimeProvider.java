package soft.eng.infrastructure;

import java.time.LocalDate;

/**
 * System implementation of date time provider.
 */
public class SystemDateTimeProvider implements DateTimeProvider {

    /**
     * Gets today's system date.
     *
     * @return today's date
     */
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}