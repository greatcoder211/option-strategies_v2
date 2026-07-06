package ownStrategy.model.strategy;

import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.OptionLeg;

import java.util.List;

public class CustomStrategy extends OptionStrategy {
    private final List<OptionLeg> optionLegs;
    //użytkownik podaje legi "jawnie", dlatego też bez sensu jest tworzyć logikę "generowania", lepiej po prostu podać je do konstruktora własnej scustomizowanej strategii
    public CustomStrategy(List<OptionLeg> optionLegs) {
        this.optionLegs = super.optionLegs;
    }
    @Override
    public List<OptionLeg> generateLegs(double spotPrice){
        //n.a.
        return null;
    }
    @Override
    public List<ChartPoint> calculatePreviewChart(Request request){
        //TODO
        return null;
    }
    @Override
    public void validateData(double spotPrice) {
        //TODO
    }
}
