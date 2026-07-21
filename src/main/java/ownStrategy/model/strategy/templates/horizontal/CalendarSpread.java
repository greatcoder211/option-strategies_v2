package ownStrategy.model.strategy.templates.horizontal;

import ownStrategy.model.OptionType;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.StrikePriceException;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.HorizontalStructure;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CalendarSpread extends NamedStrategy implements CallPutStrategy {
    private final OptionType optionType;
    private final String strategyName;
    private final double strikePrice;
    private final HorizontalStructure horizontalStructure = new HorizontalStructure();
    //z logiki musi wynikać, że tradeDate równe heute, ale trzeba przekazać mu i tak do konstruktora
    public CalendarSpread(int quantity, Belfort position, OptionType optionType, double strikePrice, LocalDate tradeDate, LocalDate shortExpiryDate, LocalDate longExpiryDate, double spotPrice) {
        super(quantity, position);
        this.strikePrice = strikePrice;
        validateData(spotPrice, List.of(tradeDate), List.of(shortExpiryDate, longExpiryDate));
        this.optionType = optionType;
        if(ChronoUnit.DAYS.between(tradeDate, shortExpiryDate) == 7 && ChronoUnit.DAYS.between(tradeDate, longExpiryDate) == 14){
            this.strategyName = "Weekly ".concat(getStrategyNameSnippet()).concat("Calendar Spread");
        }
        else if(ChronoUnit.DAYS.between(tradeDate, shortExpiryDate) == 30 && ChronoUnit.DAYS.between(tradeDate, longExpiryDate) == 60){
            this.strategyName = "Standard ".concat(getStrategyNameSnippet()).concat("Calendar Spread");
        }
        else if(ChronoUnit.DAYS.between(tradeDate, shortExpiryDate) == 30 && ChronoUnit.DAYS.between(tradeDate, longExpiryDate) == 365){
            this.strategyName = "LEAPS ".concat(getStrategyNameSnippet()).concat("Calendar Spread");
        }
        else{
            this.strategyName = getStrategyNameSnippet().concat("Calendar Spread");
        }
        super.optionLegs = this.generateLegs(spotPrice, List.of(tradeDate), List.of(shortExpiryDate, longExpiryDate));
    }

    public String getStrategyNameSnippet(){
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.CALL)){
            return "Long Call ";
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.CALL)){
            return "Short Call";
        }
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.PUT)){
            return "Long Put";
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.PUT)){
            return "Short Put";
        }
        else return "Unknown";
    }
    @Override
    public List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        List<OptionLeg> optionLegs = new ArrayList<>();
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrice, tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrice, tradeDates.get(0), expiryDates.get(1)));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrice, tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrice, tradeDates.get(0), expiryDates.get(1)));
        }
        else if(position.equals(Belfort.BUY) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrice, tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrice, tradeDates.get(0), expiryDates.get(1)));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrice, tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrice, tradeDates.get(0), expiryDates.get(1)));
        }
        return optionLegs;
    }
    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        if(strikePrice <= 0)
            throw new StrikePriceException("Wrong strike. Strike must be a positive value.");
        if(!expiryDates.get(0).isAfter(tradeDates.get(0))){
            throw new ChronologyException("The expiry searchDate should be after the trade searchDate.");
        }
    }
    @Override
    public OptionType getOptionType() {
        return optionType;
    }
}
