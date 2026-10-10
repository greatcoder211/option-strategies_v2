package ownStrategy.model.strategy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.Belfort;

import java.time.LocalDate;
import java.util.List;
@Data
public abstract class OptionStrategy {
    protected String strategyName;
    @NotNull
    protected List<OptionLeg> optionLegs;
    @NotNull
    @Min(1)
    protected final int quantity;

    public OptionStrategy(int quantity) {
        this.quantity = quantity;
    }

    public abstract void validateData(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
}