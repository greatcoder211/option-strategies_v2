package ownStrategy.model.strategy.templates.diagonal;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.DiagonalStructure;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoorMansCovered extends NamedStrategy {
    private final int quantity;
    private final Belfort position;
    private final OptionType optionType;
    private final String strategyName;
    //long powyżej i short poniżej long takze pozniej, a short- krocej(od dzisiaj)
    private final List<Double> strikePrices;
    private final LocalDate tradeDate;
    private final LocalDate shortExpiryDate;
    private final LocalDate longExpiryDate;
    private final DiagonalStructure diagonalStructure = new DiagonalStructure();
    //z logiki musi wynikać, że tradeDate równe heute, ale trzeba przekazać mu i tak do konstruktora
    public PoorMansCovered(int quantity, Belfort position, OptionType optionType, List<Double> strikePrices, LocalDate tradeDate, LocalDate shortExpiryDate, LocalDate longExpiryDate, double spotPrice) {
        this.quantity = quantity;
        this.position = position;
        this.optionType = optionType;
        this.strikePrices = strikePrices;
        this.tradeDate = tradeDate;
        this.shortExpiryDate = diagonalStructure.setExpiryDates(List.of(shortExpiryDate, longExpiryDate), -1).get(0);
        this.longExpiryDate = diagonalStructure.setExpiryDates(List.of(shortExpiryDate, longExpiryDate), -1).get(1);
        if(this.optionType.equals(OptionType.CALL)){
            this.strategyName = "Poor Man's Covered Call";
        }
        else if(this.optionType.equals(OptionType.PUT)){
            this.strategyName = "Poor Man's Covered Put";
        }
        else throw new IllegalArgumentException("Invalid option type. Either CALL or PUT");
        super.optionLegs = this.generateLegs(spotPrice);
    }
    @Override
    public List<OptionLeg> generateLegs(double spotPrice) {
        Collections.sort(strikePrices);
        List<OptionLeg> optionLegs = new ArrayList<>();
        if(position.equals(Belfort.BUY) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrices.get(0), tradeDate, longExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrices.get(1), tradeDate, shortExpiryDate));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.CALL)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, strikePrices.get(0), tradeDate, longExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, strikePrices.get(1), tradeDate, shortExpiryDate));
        }
        else if(position.equals(Belfort.BUY) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrices.get(0), tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrices.get(1), tradeDate, longExpiryDate));
        }
        else if(position.equals(Belfort.SELL) && optionType.equals(OptionType.PUT)){
            optionLegs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, strikePrices.get(0), tradeDate, shortExpiryDate));
            optionLegs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, strikePrices.get(1), tradeDate, longExpiryDate));
        }
        return optionLegs;
    }
}
