package ownStrategy.logic.sPattern;


import java.util.ArrayList;
import java.util.List;

public class OneSymmetricalSpreadStrategy implements SymmetricalSpreadStrategy {
    public List<Double> AllPrices(double price, List<Double> spreadValues, SpreadStrategy o) {
        List <Double> prices = new ArrayList<>();
        double o1 = price + spreadValues.get(0);
        double o2 = price - spreadValues.get(0);
        prices.add(o2);
        prices.add(o1);
        return prices;
    }
}
