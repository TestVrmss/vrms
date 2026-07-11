package soft.eng.infrastructure;

import java.time.LocalDate;


public class SystemDateTimeProvider implements DateTimeProvider {

   
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}