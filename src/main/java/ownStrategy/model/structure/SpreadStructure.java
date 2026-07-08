package ownStrategy.model.structure;

import java.time.LocalDate;
import java.util.List;
public interface SpreadStructure {
    List<Double> setPrices(double spotPrice, List<Double> spreads);
}
