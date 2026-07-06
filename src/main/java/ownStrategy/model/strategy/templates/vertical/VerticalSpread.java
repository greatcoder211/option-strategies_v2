package ownStrategy.model.strategy.templates.vertical;

import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.VerticalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VerticalSpread extends NamedStrategy {
    private final int quantity;
    private final Belfort position;
    private final OptionType optionType;
    private final String strategyName;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    private final double spreadValue;
    private final List<LocalDate> tradeDates;
    private final List<LocalDate> expiryDates;

    public VerticalSpread(int quantity, Belfort position, OptionType optionType, double spreadValue, LocalDate tradeDate, LocalDate expiryDate, double spotPrice) {
        this.quantity = quantity;
        super.quantity = quantity;
        this.position = position;
        this.optionType = optionType;
        if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.CALL)) {
            this.strategyName = "Bull Call Spread";
        } else if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.PUT)) {
            this.strategyName = "Bear Call Spread";
        } else if (this.position.equals(Belfort.SELL) && this.optionType.equals(OptionType.CALL)) {
            this.strategyName = "Bear Put Spread";
        } else if (this.position.equals(Belfort.SELL) && this.optionType.equals(OptionType.PUT)) {
            this.strategyName = "Bull Put Spread";
        }
        else{
            this.strategyName = "Undefined Vertical Spread";
        }
        super.optionLegs = generateLegs(spotPrice);
        this.spreadValue = spreadValue;
        this.tradeDates = verticalStructure.setTradeDates(List.of(tradeDate), 1);
        this.expiryDates = verticalStructure.setExpiryDates(List.of(expiryDate), 1);
    }

    public List<OptionLeg> generateLegs(double spotPrice) {
        List<OptionLeg> legs = new ArrayList<>();
        List<Double> prices = verticalStructure.setPrices(spotPrice, List.of(spreadValue));
        if (this.position.equals(Belfort.BUY)) {
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
        } else {
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
        }
        return legs;
    }

    @Override
    public List<ChartPoint> calculatePreviewChart(Request request){

    }
}