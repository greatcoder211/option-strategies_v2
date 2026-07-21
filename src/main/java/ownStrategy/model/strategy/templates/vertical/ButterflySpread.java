package ownStrategy.model.strategy.templates.vertical;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.SpreadException;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.VerticalStructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ButterflySpread extends NamedStrategy implements CallPutStrategy {
    @NotNull
    private final OptionType optionType;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    @NotNull
    //zakładam, że nie można zagrać zerowego spreadu, czyli de facto cztery opcje po tym samym strike'u
    @Positive
    private final double spreadValue;

    public ButterflySpread(int quantity, Belfort position, String infoLink, OptionType optionType, double spreadValue, LocalDate tradeDate, LocalDate expiryDate, double spotPrice) {
        super(quantity, position, infoLink);
        //najpierw to, bo to będziemy walidować
        this.spreadValue = spreadValue;
        //walidacja
        validateData(spotPrice, List.of(tradeDate), List.of(expiryDate));
        this.optionType = optionType;
        if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.CALL)) {
            this.strategyName = "Long Call Butterfly";
        } else if (this.position.equals(Belfort.BUY) && this.optionType.equals(OptionType.PUT)) {
            this.strategyName = "Long Put Butterfly";
        } else if(this.position.equals(Belfort.SELL)) {
            this.strategyName = "Inverse Butterfly";
        }
        else throw new IllegalArgumentException("Wrong combination. Only BUY/SELL(LONG/SHORT) and a CALL/PUT variant.");
        this.optionLegs = generateLegs(spotPrice, List.of(tradeDate), List.of(expiryDate));
    }

    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        //spread musi byc z przedzialu (0, spotPrice), expiryDate musi być za tradeDate
        if(spreadValue <= 0 || spreadValue > spotPrice){
            throw new SpreadException("Wrong spread value. Try again.");
        }
        //expiryDate musi być za tradeDate
        if(!expiryDates.get(0).isAfter(tradeDates.get(0))){
            throw new ChronologyException("The expiry searchDate should be after the trade searchDate.");
        }
        //anything else?
    }

    public List<Double> setPrices(double spotPrice, double spreadValue) {
        List<Double> prices = new ArrayList<>();
        prices.add(spotPrice - spreadValue);
        prices.add(spotPrice);
        prices.add(spotPrice);
        prices.add(spotPrice + spreadValue);
        return prices;
    }

    public List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        List<OptionLeg> legs = new ArrayList<>();
        List<Double> prices = this.setPrices(spotPrice, spreadValue);
        if (this.position.equals(Belfort.BUY)) {
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(0),  expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
        } else {
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, optionType, prices.get(2), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, optionType, prices.get(3), expiryDates.get(0), tradeDates.get(0)));
        }
        return legs;
    }

    @Override
    public OptionType getOptionType() {
        return optionType;
    }
}

