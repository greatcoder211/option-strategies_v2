package ownStrategy.logic.oldStrategy;

import java.util.List;

public interface SymmetricalSpreadStrategy {
    List<Double> AllPrices(double price, List<Double> spreadValues, SpreadStrategy strategy);
}
