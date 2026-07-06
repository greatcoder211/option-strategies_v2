package ownStrategy.model.strategy.templates.horizontal;

import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.OptionType;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.HorizontalStructure;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CalendarSpread extends NamedStrategy {
    private final int quantity;
    private final Belfort position;
    private final OptionType optionType;
    private final String strategyName;
    private final double strikePrice;
    private final LocalDate tradeDate;
    private final LocalDate shortExpiryDate;
    private final LocalDate longExpiryDate;
    private final HorizontalStructure horizontalStructure = new HorizontalStructure();
    //z logiki musi wynikać, że tradeDate równe heute, ale trzeba przekazać mu i tak do konstruktora
    public CalendarSpread(int quantity, Belfort position, OptionType optionType, double strikePrice, LocalDate tradeDate, LocalDate shortExpiryDate, LocalDate longExpiryDate, double spotPrice) {
        this.quantity = quantity;
        super.quantity = quantity;
        this.position = position;
        this.optionType = optionType;
        this.strikePrice = strikePrice;
        this.tradeDate = tradeDate;
        this.shortExpiryDate = horizontalStructure.setExpiryDates(List.of(shortExpiryDate, longExpiryDate), -1).get(0);
        this.longExpiryDate = horizontalStructure.setExpiryDates(List.of(shortExpiryDate, longExpiryDate), -1).get(1);
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
        super.optionLegs = this.generateLegs(spotPrice);
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
    public List<OptionLeg> generateLegs(double spotPrice) {
        List<OptionLeg> optionLegs = new ArrayList<>();
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrice, tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrice, tradeDate, longExpiryDate));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrice, tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrice, tradeDate, longExpiryDate));
        }
        else if(position.equals(Belfort.BUY) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrice, tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrice, tradeDate, longExpiryDate));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrice, tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrice, tradeDate, longExpiryDate));
        }
        return optionLegs;
    }

    @Override
    public List<ChartPoint> calculatePreviewChart(Request request){

    }
}
