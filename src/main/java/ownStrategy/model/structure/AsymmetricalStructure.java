package ownStrategy.model.structure;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class AsymmetricalStructure implements SpreadStructure{
    @Override
    public List<Double> setPrices(double spotPrice, List<Double> spreads){
        //dekorowane indywidualnie dla każdej strategii
        return null;
    }
    @Override
    public List<LocalDate> setTradeDates(List<LocalDate> tradeDates, int spreadNumber){
        List<LocalDate> resultTradeDates = new ArrayList<>();
        for(int i = 0; i < spreadNumber; i++){
            resultTradeDates.add(resultTradeDates.get(0));
        }
        return resultTradeDates;
    }
    @Override
    public List<LocalDate> setExpiryDates(List<LocalDate> expiryDates, int spreadNumber) {
        List<LocalDate> resultExpiryDates = new ArrayList<>();
        for(int i = 0; i < spreadNumber; i++){
            resultExpiryDates.add(resultExpiryDates.get(0));
        }
        return resultExpiryDates;
    }
}
