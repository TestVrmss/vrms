package soft.eng.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import soft.eng.domain.model.Rental;


public final class StandardRentalPricingStrategy implements RentalPricingStrategy {

    public static final BigDecimal DEFAULT_LATE_PENALTY_RATE = new BigDecimal("0.25");

    private final BigDecimal latePenaltyRate;


    public StandardRentalPricingStrategy() {
        this(DEFAULT_LATE_PENALTY_RATE);
    }

   
    
    public StandardRentalPricingStrategy(BigDecimal latePenaltyRate) {
        Objects.requireNonNull(latePenaltyRate, "latePenaltyRate must not be null");
        if (latePenaltyRate.signum() < 0) {
            throw new IllegalArgumentException("latePenaltyRate must not be negative");
        }
        this.latePenaltyRate = latePenaltyRate;
    }


    @Override
    public BigDecimal calculateCost(Rental rental, LocalDate actualReturnDate) {
        Objects.requireNonNull(rental, "rental must not be null");
        Objects.requireNonNull(actualReturnDate, "actualReturnDate must not be null");
        if (actualReturnDate.isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("actualReturnDate must not be before rental start");
        }

        BigDecimal dailyRate = rental.getVehicle().getDailyRate();
        BigDecimal baseCost = dailyRate.multiply(BigDecimal.valueOf(rental.getRentalDays()));
        BigDecimal lateCost = dailyRate
                .multiply(latePenaltyRate)
                .multiply(BigDecimal.valueOf(rental.getLateDays(actualReturnDate)));
        return baseCost.add(lateCost).setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal getLatePenaltyRate() {
        return latePenaltyRate;
    }
}
