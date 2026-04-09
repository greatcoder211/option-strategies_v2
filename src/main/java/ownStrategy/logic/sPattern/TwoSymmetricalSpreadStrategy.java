package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.List;

public class TwoSymmetricalSpreadStrategy implements SymmetricalSpreadStrategy {
    public List<Double> AllPrices(double price, List<Double> spreadValues, SpreadStrategy o){
        List<Double> prices = new ArrayList<>();
        Double d1 = price - spreadValues.get(1);
        Double d2 = price - spreadValues.get(0);
        Double d3 = price + spreadValues.get(0);
        Double d4 = price + spreadValues.get(1);
        prices.add(d1);
        prices.add(d2);
        prices.add(d3);
        prices.add(d4);
        return prices;
    }
}
