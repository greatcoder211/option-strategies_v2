package ownStrategy.logic.sPattern;

public enum StrategyType {
    Butterfly_Spread(ButterflySpread.class),
    Vertical_Spread(VerticalSpread.class),
    Ratio_Spread(RatioSpread.class),
    Iron_Condor(IronCondor.class),
    Iron_Butterfly(IronButterfly.class),
    Strangle(Strangle.class);

    private final Class<? extends SpreadStrategy> strategyClass;

    StrategyType(Class<? extends SpreadStrategy> strategyClass) {
        this.strategyClass = strategyClass;
    }

    public Class<? extends SpreadStrategy> getStrategyClass() {
        return strategyClass;
    }
}
