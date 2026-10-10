package ownStrategy.model.strategy;

import ownStrategy.exception.ChronologyException;
import ownStrategy.model.entity.portfolio.OptionLeg;

import java.time.LocalDate;
import java.util.List;
public class CustomStrategy extends OptionStrategy {
    public List<OptionLeg> optionLegs;
    public CustomStrategy(int quantity, List<OptionLeg> optionLegs) {
        super(quantity);
        this.optionLegs = optionLegs;
    }
    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates){
        if(spotPrice <= 0){
            throw new IllegalArgumentException("Wrong price. Only positive values.");
        }
        for(OptionLeg optionLeg : optionLegs){
            if(optionLeg.expiryDate().isBefore(optionLeg.tradeDate())){
                throw new ChronologyException("Expiry date cannot be before trade date.");
            }
        }
    }
}
