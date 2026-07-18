package ownStrategy.model.strategy;

import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;

import java.time.LocalDate;
import java.util.List;
public class CustomStrategy extends OptionStrategy {
    public CustomStrategy(int quantity, Belfort position) {
        super(quantity, position);
    }
    @Override
    public List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates){
        //n.a.
        return null;
    }
    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates){
        //TODO
    }
}
