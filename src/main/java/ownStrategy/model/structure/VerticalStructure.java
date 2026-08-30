package ownStrategy.model.structure;
import java.time.LocalDate;
import java.util.*;
public class VerticalStructure implements SpreadStructure {
    @Override
    public List<Double> setPrices(double spotPrice, List<Double> spreads){
        int numberOfSpreads = spreads.size();
        List<Double> prices = new ArrayList<>();
        for(int i = 0; i < numberOfSpreads; i++){
            prices.add(spotPrice - spreads.get(i));
            prices.add(spotPrice + spreads.get(i));
        }
        Collections.sort(prices);
        return prices;
    }
//poniższe dwie- podajemy de facto listę z jednej daty, bo data w strategiach wertykalnych jest równa dla wszystkich legów. Tylko pod indeksem 0 mamy datę, która nas interesuje
    //nieaktualne- te metody były useless, bo LocalDate jest częścią każdego OptionLegu
}