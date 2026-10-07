package ownStrategy.model.strategy;

import java.util.List;

public interface SpreadStrategy {
    List<Double> setPrices(double spotPrice);
}
//tam gdzie mamy jakieś spready i definiowanie rozkładu strike'ów w legach przez spready, takie strategie niech będą "Strategiami Spreadowymi"