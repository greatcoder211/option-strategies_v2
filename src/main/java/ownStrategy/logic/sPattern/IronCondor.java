package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.List;

public class IronCondor extends SpreadStrategy {
//    private String name;
    private final int spreadNumber = 2;
//    private final OptionType type = OptionType.NA;

    public IronCondor(String name, Belfort LS) {
        super(name, LS);
    }

    public IronCondor(){}

    @Override
    public boolean getCP() { return false; }

    @Override
    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public void setName() {
        if(this.LongOrShort().equals(Belfort.BUY)){
            this.name = "Reverse Iron Condor";
        }
        else{
            this.name = "Iron Condor";
        }
    }


    @Override
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        List <Double> Condor = new ArrayList<>();
        TwoSymmetricalSpreadStrategy beta = new TwoSymmetricalSpreadStrategy();
        Condor = beta.AllPrices(price, spreads, o);
        return Condor;
    }

    @Override
    public List <OptionLeg> setOptionLegs(List<Double> prices){
        List <OptionLeg> legs = new ArrayList<>();
        if(this.LongOrShort().equals(Belfort.BUY)){
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(1), OptionType.PUT, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(2), OptionType.CALL, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(3), OptionType.CALL, Belfort.SELL));
        }
        else{
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(1), OptionType.PUT, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(2), OptionType.CALL, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(3), OptionType.CALL, Belfort.BUY));
        }
        return legs;
    }
}

