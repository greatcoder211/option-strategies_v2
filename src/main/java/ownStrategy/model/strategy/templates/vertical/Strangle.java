package ownStrategy.model.strategy.templates.vertical;

import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.SpreadException;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.VerticalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Strangle extends NamedStrategy {
    private final String strategyName;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    private final double spreadValue;

    public Strangle(int quantity, Belfort position, double spreadValue, LocalDate tradeDate, LocalDate expiryDate, double spotPrice){
        super(quantity, position);
        this.spreadValue = spreadValue;
        validateData(spotPrice, List.of(tradeDate), List.of(expiryDate));
        this.strategyName = this.position.equals(Belfort.BUY) ? "Long Strangle" : "Short Strangle";
        super.optionLegs = generateLegs(spotPrice, List.of(tradeDate), List.of(expiryDate));
    }

    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        //spread musi byc z przedzialu (0, spotPrice), expiryDate musi być za tradeDate
        if(spreadValue <= 0 || spreadValue > spotPrice){
            throw new SpreadException("Wrong spread value. Try again.");
        }
        //expiryDate musi być za tradeDate
        if(!expiryDates.get(0).isAfter(tradeDates.get(0))){
            throw new ChronologyException("The expiry date should be after the trade date.");
        }
        //anything else?
    }

    @Override
    public List <OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates){
        List<Double> prices = verticalStructure.setPrices(spotPrice, List.of(spreadValue));
        List <OptionLeg> legs = new ArrayList<>();
        if(this.position.equals(Belfort.BUY)){
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
        }
        else{
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
        }
        return legs;
    }
}

