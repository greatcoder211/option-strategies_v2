package ownStrategy.model.strategy.templates.asymmetrical;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.AsymmetricalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatioSpread extends NamedStrategy {
    private final int quantity;
    private final Belfort position;
    private final OptionType optionType;
    private final String strategyName;
    private final AsymmetricalStructure asymmetricalStructure = new AsymmetricalStructure();
    private final double spreadValue;
    private final List<LocalDate> tradeDates;
    private final List<LocalDate> expiryDates;

    public RatioSpread(int quantity, Belfort position, OptionType optionType, double spreadValue, LocalDate tradeDate, LocalDate expiryDate, double spotPrice){
        this.quantity = quantity;
        this.position = position;
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
        super.optionLegs = generateLegs(spotPrice);
        this.spreadValue = spreadValue;
        this.tradeDates = asymmetricalStructure.setTradeDates(List.of(tradeDate), 1);
        this.expiryDates = asymmetricalStructure.setExpiryDates(List.of(expiryDate), 1);;
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
    public List <OptionLeg> generateLegs(double spotPrice){
        List <OptionLeg> legs = new ArrayList<>();
        List<Double> prices = this.setPrices(spotPrice, spreadValue);
        if(this.position.equals(Belfort.BUY)){
            if(this.optionType.equals(OptionType.PUT)){
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            }
            else if(this.optionType.equals(OptionType.CALL)){
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            }
        }
        else{
            if(this.optionType.equals(OptionType.PUT)){
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            }
            else if(this.optionType.equals(OptionType.CALL)){
                legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
                legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            }
        }
        return legs;
    }
}
