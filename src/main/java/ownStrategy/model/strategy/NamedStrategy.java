package ownStrategy.model.strategy;
import jakarta.validation.constraints.NotNull;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;

import java.time.LocalDate;
import java.util.List;

public abstract class NamedStrategy extends OptionStrategy {
    protected final String infoLink;

    public NamedStrategy(int quantity, Belfort position, String infoLink){
        super(quantity, position);
        this.infoLink = infoLink;
    }

    public abstract List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
    public abstract void validateData(double spotPrice, List<LocalDate> tradeDates,  List<LocalDate> expiryDates);
}