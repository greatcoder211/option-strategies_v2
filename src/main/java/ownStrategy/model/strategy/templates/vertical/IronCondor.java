package ownStrategy.model.strategy.templates.vertical;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.VerticalStructure;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class IronCondor extends NamedStrategy {
    private final int quantity;
    private final Belfort position;
    private final String name;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    private final List<Double> spreadValues;
    private final List<LocalDate> tradeDates;
    private final List<LocalDate> expiryDates;
//although we need only one date, I make a sN-fold(spreadNumber) list of them so as to keep to the standard of having List of LocalDates which can vary depending on the strategy and its category
    public IronCondor(int quantity, Belfort position, List<Double> spreadValues, LocalDate tradeDate, LocalDate expiryDate, double spotPrice){
        this.quantity = quantity;
        this.position = position;
        this.name = position.equals(Belfort.BUY) ? "Reverse Iron Butterfly" : "Iron Butterfly";
        super.optionLegs = generateLegs(spotPrice);
        this.spreadValues = spreadValues;
        this.tradeDates = verticalStructure.setTradeDates(List.of(tradeDate), spreadValues.size());
        this.expiryDates = verticalStructure.setExpiryDates(List.of(expiryDate), spreadValues.size());;
    }

    @Override
    public List <OptionLeg> generateLegs(double spotPrice){
        List <OptionLeg> legs = new ArrayList<>();
        List<Double> prices = verticalStructure.setPrices(spotPrice, spreadValues);
        if(position.equals(Belfort.BUY)){
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(3), expiryDates.get(3), tradeDates.get(3)));
        }
        else{
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(1), expiryDates.get(1), tradeDates.get(1)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(2), expiryDates.get(2), tradeDates.get(2)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(3), expiryDates.get(3), tradeDates.get(3)));
        }
        return legs;
    }
}