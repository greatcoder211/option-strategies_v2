package ownStrategy.model.strategy.templates.diagonal;
import ownStrategy.dto.OptionType;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.StrikePriceException;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.DiagonalStructure;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoorMansCovered extends NamedStrategy implements CallPutStrategy {
    private final OptionType optionType;
    private final String strategyName;
    //long powyżej i short poniżej long takze pozniej, a short- krocej(od dzisiaj)
    private final List<Double> strikePrices;
    private final DiagonalStructure diagonalStructure = new DiagonalStructure();
    //z logiki musi wynikać, że tradeDate równe heute, ale trzeba przekazać mu i tak do konstruktora
    //two expiry: short/long expiry
    public PoorMansCovered(int quantity, Belfort position, OptionType optionType, List<Double> strikePrices, LocalDate tradeDate, List<LocalDate> expiryDates, double spotPrice) {
        super(quantity, position);
        this.strikePrices = strikePrices;
        validateData(spotPrice, List.of(tradeDate),  expiryDates);
        this.optionType = optionType;
        if(this.optionType.equals(OptionType.CALL)){
            this.strategyName = "Poor Man's Covered Call";
        }
        else if(this.optionType.equals(OptionType.PUT)){
            this.strategyName = "Poor Man's Covered Put";
        }
        else throw new IllegalArgumentException("Invalid option type. Either CALL or PUT");
        super.optionLegs = generateLegs(spotPrice, List.of(tradeDate), expiryDates);
    }
    @Override
    public List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        Collections.sort(strikePrices);
        Collections.sort(expiryDates);
        List<OptionLeg> optionLegs = new ArrayList<>();
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrices.get(0), tradeDates.get(0), expiryDates.get(1)));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrices.get(1), tradeDates.get(0), expiryDates.get(0)));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrices.get(0), tradeDates.get(0), expiryDates.get(1)));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrices.get(1), tradeDates.get(0), expiryDates.get(0)));
        }
        else if(position.equals(Belfort.BUY) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrices.get(0), tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrices.get(1), tradeDates.get(0), expiryDates.get(1)));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrices.get(0), tradeDates.get(0), expiryDates.get(0)));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrices.get(1), tradeDates.get(0), expiryDates.get(1)));
        }
        return optionLegs;
    }

    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        for(double strikePrice: strikePrices){
            if(strikePrice <= 0)
                throw new StrikePriceException("Wrong strike. Strike must be a positive value.");
        }
        for(LocalDate expiryDate: expiryDates){
            if(!expiryDate.isAfter(tradeDates.get(0))){
                throw new ChronologyException("The expiry date should be after the trade date.");
            }
        }
    }

    @Override
    public OptionType getOptionType() {
        return optionType;
    }
}
