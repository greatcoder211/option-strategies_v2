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
}
