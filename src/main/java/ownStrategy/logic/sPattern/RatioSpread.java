package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.List;

public class RatioSpread extends SpreadStrategy {
//    private String name;
    private final int spreadNumber = 1;

    public RatioSpread(String name, Belfort LS) {
        super(name, LS);
    }

    @Override
    public boolean getCP() { return true; }

    @Override
    public OptionType getType(){ return super.getType(); }

    @Override
    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public void setName() {
        if(super.LongOrShort().equals(Belfort.BUY) && super.getType().equals(OptionType.CALL)){
            super.name = "Call Ratio Spread";
        }
        else if(super.LongOrShort().equals(Belfort.BUY) && super.getType().equals(OptionType.PUT)){
            super.name = "Put Ratio Spread";
        }
        else if(super.LongOrShort().equals(Belfort.SELL) && super.getType().equals(OptionType.CALL)){
            super.name = "Call Backspread";
        }
        else if(super.LongOrShort().equals(Belfort.SELL) && super.getType().equals(OptionType.PUT)){
            super.name = "Put Backspread";
        }
    }

    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o){
        List<Double> prices = new ArrayList<>();
        prices.add(price);
        return prices;
    }

    @Override
    public List<OptionLeg> setOptionLegs(List<Double> prices) {
        double spread = super.getSpreads().get(0);
        List<OptionLeg> legs = new ArrayList<>();
        if (super.LongOrShort().equals(Belfort.BUY)) {
            if (getType().equals(OptionType.PUT)) {
                legs.add(new OptionLeg(prices.get(0) - spread, OptionType.PUT, Belfort.SELL));
                legs.add(new OptionLeg(prices.get(0) - spread, OptionType.PUT, Belfort.SELL));
                legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.BUY));

            } else {
                legs.add(new OptionLeg(prices.get(0), OptionType.CALL, Belfort.BUY));
                legs.add(new OptionLeg(prices.get(0) + spread, OptionType.CALL, Belfort.SELL));
                legs.add(new OptionLeg(prices.get(0) + spread, OptionType.CALL, Belfort.SELL));
            }
            return legs;
        } else {
            if (getType().equals(OptionType.PUT)) {
                legs.add(new OptionLeg(prices.get(0) - spread, OptionType.PUT, Belfort.BUY));
                legs.add(new OptionLeg(prices.get(0) - spread, OptionType.PUT, Belfort.BUY));
                legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.SELL));
            } else {
                legs.add(new OptionLeg(prices.get(0), OptionType.CALL, Belfort.SELL));
                legs.add(new OptionLeg(prices.get(0) + spread, OptionType.CALL, Belfort.BUY));
                legs.add(new OptionLeg(prices.get(0) + spread, OptionType.CALL, Belfort.BUY));
            }
        }
        return legs;
    }
}
