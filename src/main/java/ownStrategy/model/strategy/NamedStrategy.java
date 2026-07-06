package ownStrategy.model.strategy;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class NamedStrategy extends OptionStrategy {
    protected List<OptionLeg> optionLegs = new ArrayList<>();
    public NamedStrategy(){
        this.optionLegs = super.optionLegs;
    }
    public abstract List<OptionLeg> generateLegs(double spotPrice);
}
