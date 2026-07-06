package ownStrategy.model.strategy;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;
import java.util.List;

public abstract class NamedStrategy extends OptionStrategy {
    public NamedStrategy(int quantity, Belfort position) {
        super(quantity, position);
    }
    public abstract List<OptionLeg> generateLegs(double spotPrice);
    public abstract List<ChartPoint> calculatePreviewChart(Request request);
    public abstract void validateData(double spotPrice);
}
