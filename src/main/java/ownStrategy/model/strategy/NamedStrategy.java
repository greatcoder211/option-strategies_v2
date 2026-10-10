package ownStrategy.model.strategy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;

import java.time.LocalDate;
import java.util.List;

public abstract class NamedStrategy extends OptionStrategy {
    @NotNull
    protected final Belfort position;
    public NamedStrategy(int quantity, Belfort position){
        super(quantity);
        this.position = position;
    }
    public abstract List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
    public abstract void validateData(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
}