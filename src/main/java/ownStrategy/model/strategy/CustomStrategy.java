package ownStrategy.model.strategy;

import ownStrategy.model.OptionLeg;

import java.time.LocalDate;
import java.util.List;
public class CustomStrategy extends OptionStrategy {
    public CustomStrategy(List<OptionLeg> optionLegs) {
        this.optionLegs = optionLegs;
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
