package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IronButterfly extends SpreadStrategy {
//    private String name;
    private final int spreadNumber = 2;
//    private final OptionType type = OptionType.NA;


    public IronButterfly(String name, Belfort LS) {
        super(name, LS);
    }

    public IronButterfly() {}

    @Override
    public boolean getCP() { return false; }

    @Override
    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public String getName() { return this.name; }

    @Override
    public void setName() {
        if(super.LongOrShort().equals(Belfort.BUY)){
            super.name = "Reverse Iron Butterfly";
        }
        else{
            super.name = "Iron Butterfly";
        }
    }

    @Override
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        List <Double> IronButt = new ArrayList<>();
        OneSymmetricalSpreadStrategy alfa = new OneSymmetricalSpreadStrategy();
        IronButt = alfa.AllPrices(price, spreads, o);
        IronButt.add(price);
        IronButt.add(price);
        Collections.swap(IronButt, 1, 3);
        return IronButt;
    }

    @Override
    public List <OptionLeg> setOptionLegs(List<Double> prices){
        List <OptionLeg> legs = new ArrayList<>();
        if(super.LongOrShort().equals(Belfort.BUY)){
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(1), OptionType.PUT, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(2), OptionType.CALL, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(3), OptionType.CALL, Belfort.SELL ));
            return legs;
        }
        else{
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(1), OptionType.PUT, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(2), OptionType.CALL, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(3), OptionType.CALL, Belfort.BUY));
            return legs;
        }
    }
}
