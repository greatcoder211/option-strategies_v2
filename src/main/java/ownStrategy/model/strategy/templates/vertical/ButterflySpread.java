package ownStrategy.model.strategy.templates.vertical;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.ButterflySpreadRequest;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.SpreadException;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.structure.VerticalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ButterflySpread extends NamedStrategy {
    @NotNull
    private final OptionType optionType;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    @NotNull
    //zakładam, że nie można zagrać zerowego spreadu, czyli de facto cztery opcje po tym samym strike'u
    @Positive
    private final double spreadValue;

    public ButterflySpread(int quantity, Belfort position, OptionType optionType, double spreadValue, LocalDate tradeDate, LocalDate expiryDate, double spotPrice) {
        super(quantity, position);
        //najpierw to, bo to będziemy walidować
        this.spreadValue = spreadValue;
        this.tradeDates = verticalStructure.setTradeDates(List.of(tradeDate), 1);
        this.expiryDates = verticalStructure.setExpiryDates(List.of(expiryDate), 1);
        //walidacja
        validateData(spotPrice);
        this.optionType = optionType;
        if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.CALL)) {
            this.strategyName = "Long Call Butterfly";
        } else if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.PUT)) {
            this.strategyName = "Long Put Butterfly";
        } else if(this.position.equals(Belfort.SELL)) {
            this.strategyName = "Inverse Butterfly";
        }
        else throw new IllegalArgumentException("Wrong combination. Only BUY/SELL(LONG/SHORT) and a CALL/PUT variant.");
        this.optionLegs = generateLegs(spotPrice);
    }

    @Override
    public void validateData(double spotPrice) {
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

    public List<ChartPoint> chart(OptionStrategy strategy, double spotPrice) {
        List<ChartPoint> points = new ArrayList<>();
        try {
            points = chartGenerator.draw(strategy, spotPrice);
        } catch (Exception e) { e.printStackTrace();}
        return points;
    }

    @Override
    public List<ChartPoint> calculatePreviewChart(Request request){

    }

    public List<Double> setPrices(double spotPrice, double spreadValue) {
        List<Double> prices = new ArrayList<>();
        prices.add(spotPrice - spreadValue);
        prices.add(spotPrice);
        prices.add(spotPrice);
        prices.add(spotPrice + spreadValue);
        return prices;
    }

    public List<OptionLeg> generateLegs(double spotPrice) {
        List<OptionLeg> legs = new ArrayList<>();
        List<Double> prices = this.setPrices(spotPrice, spreadValue);
        if (this.position.equals(Belfort.BUY)) {
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(0), expiryDates.get(2), tradeDates.get(2)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(1), expiryDates.get(3), tradeDates.get(3)));
        } else {
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(3), expiryDates.get(3), tradeDates.get(3)));
        }
        return legs;
    }
}

