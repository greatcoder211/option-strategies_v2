package ownStrategy.model.strategy.templates.vertical;
import ownStrategy.exception.ChronologyException;
import ownStrategy.exception.SpreadException;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.strategy.NamedStrategy;
import ownStrategy.model.structure.VerticalStructure;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class IronCondor extends NamedStrategy {
    private final String name;
    private final VerticalStructure verticalStructure = new VerticalStructure();
    private final List<Double> spreadValues;

    //although we need only one searchDate, I make a sN-fold(spreadNumber) list of them so as to keep to the standard of having List of LocalDates which can vary depending on the strategy and its category
    public IronCondor(int quantity, Belfort position, List<Double> spreadValues, LocalDate tradeDate, LocalDate expiryDate, double spotPrice) {
        super(quantity, position);
        this.spreadValues = spreadValues;
        validateData(spotPrice, List.of(tradeDate), List.of(expiryDate));
        this.name = position.equals(Belfort.BUY) ? "Reverse Iron Butterfly" : "Iron Butterfly";
        super.optionLegs = generateLegs(spotPrice, List.of(tradeDate), List.of(expiryDate));
    }

    @Override
    public void validateData(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        //user enters it as: index = (0: lower: 1 higher), so we validate higher as normal spread, lower just has to be lower
        if (spreadValues.get(1) <= 0 || spreadValues.get(1) > spotPrice) {
            throw new SpreadException("Wrong spread value. Try again.");
        } else if (spreadValues.get(0) > spreadValues.get(1)) {
            throw new SpreadException("Wrong spread value. Lower spread has to be lower.");
        }

        //expiryDate musi być za tradeDate
        if (!expiryDates.get(0).isAfter(tradeDates.get(0))) {
            throw new ChronologyException("The expiry searchDate should be after the trade searchDate.");
        }
        //anything else? maybe number format?
    }

    @Override
    public List<OptionLeg> generateLegs(double spotPrice, List<LocalDate> tradeDates, List<LocalDate> expiryDates) {
        List<OptionLeg> legs = new ArrayList<>();
        List<Double> prices = verticalStructure.setPrices(spotPrice, spreadValues);
        if (position.equals(Belfort.BUY)) {
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(2), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(3), expiryDates.get(0), tradeDates.get(0)));
        } else {
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.PUT, prices.get(0), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.PUT, prices.get(1), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.SELL, OptionType.CALL, prices.get(2), expiryDates.get(0), tradeDates.get(0)));
            legs.add(new OptionLeg(quantity, Belfort.BUY, OptionType.CALL, prices.get(3), expiryDates.get(0), tradeDates.get(0)));
        }
        return legs;
    }
}