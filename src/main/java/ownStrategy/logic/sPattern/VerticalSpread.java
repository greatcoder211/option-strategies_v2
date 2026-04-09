package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.List;

public class VerticalSpread extends SpreadStrategy {
//    private String name;
    private final int spreadNumber = 1;
//    private OptionType type;

    public VerticalSpread(String name, Belfort LS) {
        super(name, LS);
    }

    public VerticalSpread() {}

    @Override
    public boolean getCP() { return true; }

    @Override
    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public OptionType getType() { return super.getType(); }

    @Override
    public void setName() {
        if(super.LongOrShort().equals(Belfort.BUY) && super.getType().equals(OptionType.CALL)){
            super.name = "Bull Call Spread";
        }
        else if(this.LongOrShort().equals(Belfort.SELL) && this.getType().equals(OptionType.CALL)){
            super.name = "Bear Call Spread";
        }
        else if(this.LongOrShort().equals(Belfort.BUY) && this.getType().equals(OptionType.PUT)){
            super.name = "Bear Put Spread";
        }
        else if(this.LongOrShort().equals(Belfort.SELL) && this.getType().equals(OptionType.PUT)){
            super.name = "Bull Put Spread";
        }
    }

    @Override
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        List <Double> vertical = new ArrayList<>();
        OneSymmetricalSpreadStrategy alfa = new OneSymmetricalSpreadStrategy();
        vertical = alfa.AllPrices(price, spreads, o);
        return vertical;
    }

    @Override
    public List <OptionLeg> setOptionLegs(List<Double> prices){
        OptionType type = super.getType();
        List <OptionLeg> legs = new ArrayList<>();
        if(super.LongOrShort().equals(Belfort.BUY)){
            legs.add(new OptionLeg(prices.get(0), type, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(1), type, Belfort.SELL));
        }
        else{
            legs.add(new OptionLeg(prices.get(0), type, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(1), type, Belfort.BUY));
        }
        return legs;
    }
}
