package ownStrategy.model.structure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiagonalStructure implements SpreadStructure{
    @Override
    public List<Double> setPrices(double spotPrice, List<Double> spreads) {
        return List.of();
    }

    @Override
    public List<LocalDate> setTradeDates(List<LocalDate> tradeDates, int spreadNumber) {
        return tradeDates;
    }

    @Override
    public List<LocalDate> setExpiryDates(List<LocalDate> expiryDates, int spreadNumber) {
        return expiryDates;
    }
}
