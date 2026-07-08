package ownStrategy.model.strategy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.logic.finance.BlackScholesUtils;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.PricingContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Data
public abstract class OptionStrategy {
    @NotNull
    @Min(1)
    protected final int quantity;
    @NotNull
    protected final Belfort position;
    protected String strategyName;
    @NotNull
    protected List<OptionLeg> optionLegs;

    public OptionStrategy(int quantity, Belfort position) {
        this.quantity = quantity;
        this.position = position;
    }

    public abstract List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates);

    public abstract void validateData(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
}