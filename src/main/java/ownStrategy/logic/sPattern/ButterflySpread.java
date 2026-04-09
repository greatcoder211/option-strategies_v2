package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButterflySpread extends SpreadStrategy {
//    private String name;
//    private OptionType type;
    private final int spreadNumber = 1;

    public ButterflySpread(String name, Belfort LS) {
        super(name, LS);
    }

    public ButterflySpread() {}

    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public boolean getCP() { return true; }

    @Override
    public OptionType getType() { return super.getType(); }

    @Override
    public void setName() {
        if(this.LongOrShort().equals(Belfort.BUY) && this.type.equals(OptionType.CALL)){
            this.name = "Long Call Butterfly";
        }
        else if(this.LongOrShort().equals(Belfort.BUY) && this.type.equals(OptionType.PUT)){
            this.name = "Long Put Butterfly";
        }
        else{
            this.name = "Inverse Butterfly";
        }
    }

    @Override
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        List <Double> butterfly = new ArrayList<>();
        OneSymmetricalSpreadStrategy alfa = new OneSymmetricalSpreadStrategy();
        butterfly = alfa.AllPrices(price, spreads, o);
        butterfly.add(price);
        butterfly.add(price);
        Collections.swap(butterfly, 1, 3);
        return butterfly;
    }

    @Override
    public List <OptionLeg> setOptionLegs(List<Double> prices){
        OptionType type = this.getType();
        List <OptionLeg> legs = new ArrayList<>();
        if(this.LongOrShort().equals(Belfort.BUY)){
            legs.add(new OptionLeg(prices.get(0), type, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(1), type, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(2), type, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(3), type, Belfort.BUY));
        }
        else{
            legs.add(new OptionLeg(prices.get(0), type, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(1), type, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(2), type, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(3), type, Belfort.SELL));
        }
        return legs;
    }
}

