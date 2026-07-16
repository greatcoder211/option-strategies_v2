package ownStrategy.logic.finance;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ownStrategy.config.DefaultPricingContext;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.PricingContext;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor(force = true)
@Component
public class ChartGenerator {
    private final DefaultPricingContext defaultPricingContext;
    public ChartGenerator(DefaultPricingContext defaultPricingContext) {
        this.defaultPricingContext = defaultPricingContext;
    }
    public List<ChartPoint> draw(double spotPrice, List<OptionLeg> optionLegs) {
        List <ChartPoint> chartPoints = new ArrayList<>();
        double bottom = 0.8 * optionLegs.get(0).strikePrice();
        double top = 1.2 * optionLegs.get(optionLegs.size() - 1).strikePrice();
        double totalRange = top - bottom;
        for(int i = 0; i < 100; i++){
            double pricePoint = bottom + totalRange * i / 100;
            chartPoints.add(new ChartPoint(pricePoint, StrategyCalculator.calculatePnL(optionLegs, spotPrice, pricePoint, new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility()))));
        }
        return chartPoints;
    }
}
