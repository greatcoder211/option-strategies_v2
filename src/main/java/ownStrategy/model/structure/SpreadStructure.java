package ownStrategy.model.structure;

import java.time.LocalDate;
import java.util.List;
public interface SpreadStructure {
    List<Double> setPrices(double spotPrice, List<Double> spreads);
    List<LocalDate> setTradeDates(List<LocalDate> tradeDates, int spreadNumber);
    List<LocalDate> setExpiryDates(List<LocalDate> expiryDates, int spreadNumber);
}
