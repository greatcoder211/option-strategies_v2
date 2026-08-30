package ownStrategy.model.structure;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public class HorizontalStructure implements SpreadStructure{

    @Override
    public List<Double> setPrices(double spotPrice, List<Double> spreads) {
        return List.of();
    }

}
