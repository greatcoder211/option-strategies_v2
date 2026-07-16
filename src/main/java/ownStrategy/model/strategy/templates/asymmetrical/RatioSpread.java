package ownStrategy.model.strategy.templates.asymmetrical;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.SpreadException;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.OptionType;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.AsymmetricalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatioSpread extends NamedStrategy implements CallPutStrategy {
    private final OptionType optionType;
    private final String strategyName;
    private final AsymmetricalStructure asymmetricalStructure = new AsymmetricalStructure();
    private final double spreadValue;

    public RatioSpread(int quantity, Belfort position, OptionType optionType, double spreadValue, double spotPrice, LocalDate tradeDate, LocalDate expiryDate) {
        super(quantity, position);
        this.spreadValue = spreadValue;
        validateData(spotPrice, List.of(tradeDate), List.of(expiryDate));
        this.optionType = optionType;
        if(this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.CALL)){
            this.strategyName = "Call Ratio Spread";
        }
        else if(this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.PUT)){
            this.strategyName = "Put Ratio Spread";
        }
        else if(this.position.equals(Belfort.SELL) && this.optionType.equals(OptionType.CALL)){
            this.strategyName = "Call Backspread";
        }
        else if(this.position.equals(Belfort.SELL) && this.optionType.equals(OptionType.PUT)){
            this.strategyName = "Put Backspread";
        }
        else{
            this.strategyName = "Undefined Ratio Spread";
        }
        super.optionLegs = generateLegs(spotPrice, List.of(tradeDate), List.of(expiryDate));
    }

    public List<Double> setPrices(double spotPrice, double spreadValue) {
        List<Double> prices = new ArrayList<>();
        prices.add(spotPrice);
        prices.add(this.optionType.equals(OptionType.PUT) ? spotPrice - spreadValue : spotPrice + spreadValue);
        prices.add(this.optionType.equals(OptionType.PUT) ? spotPrice - spreadValue : spotPrice + spreadValue);
        Collections.sort(prices);
        return prices;
    }

    @Override
    public List <OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        List <OptionLeg> legs = new ArrayList<>();
        List<Double> prices = this.setPrices(spotPrice, spreadValue);
        if(this.position.equals(Belfort.BUY)){
            if(this.optionType.equals(OptionType.PUT)){
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(2), expiryDates.get(0), tradeDates.get(0)));
            }
            else if(this.optionType.equals(OptionType.CALL)){
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(2), expiryDates.get(0), tradeDates.get(0)));
            }
        }
        else{
            if(this.optionType.equals(OptionType.PUT)){
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(1), expiryDates.get(1), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(2), expiryDates.get(2), tradeDates.get(0)));
            }
            else if(this.optionType.equals(OptionType.CALL)){
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(1), expiryDates.get(1), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(2), expiryDates.get(2), tradeDates.get(0)));
            }
        }
        return legs;
    }

    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates){
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
    public OptionType getOptionType() {
        return optionType;
    }
}
