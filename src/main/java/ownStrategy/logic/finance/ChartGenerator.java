package ownStrategy.logic.finance;

import lombok.NoArgsConstructor;
import ownStrategy.dto.ChartPoint;
import ownStrategy.model.strategy.OptionStrategy;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor(force = true)
public class ChartGenerator {
    private OptionCalculator optionCalculator;
    public ChartGenerator(OptionCalculator optionCalculator) {
        this.optionCalculator = optionCalculator;
    }
    public List<ChartPoint> draw(OptionStrategy optionStrategy, double spotPrice) {
        List <ChartPoint> chartPoints = new ArrayList<>();
        double bottom = 0.8 * optionStrategy.getOptionLegs().get(0).strikePrice();
        double top = 1.2 * optionStrategy.getOptionLegs().get(optionStrategy.getOptionLegs().size() - 1).strikePrice();
        double totalRange = top - bottom;
        for(int i = 0; i < 100; i++){
            double pricePoint = bottom + totalRange * i / 100;
            chartPoints.add(new ChartPoint(pricePoint, optionCalculator.function(optionStrategy, pricePoint)));
        }
        return chartPoints;
    }
}
