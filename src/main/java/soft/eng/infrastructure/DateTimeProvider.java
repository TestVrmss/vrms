package soft.eng.infrastructure;

import java.time.LocalDate;

/**
 * Provides date values to make date-based logic testable.
 */
public interface DateTimeProvider {

    /**
     * Gets today's date.
     *
     * @return today's date
     */
    LocalDate today();
}