package ownStrategy.logic.sPattern;

import java.util.ArrayList;
import java.util.List;

public class Strangle extends SpreadStrategy {
//    private String name;
    private final int spreadNumber = 1;
//    private final OptionType type = OptionType.NA;

    public Strangle(String name, Belfort LS) {
        super(name, LS);
    }

    public Strangle(){}

    @Override
    public boolean getCP() { return false; }

    @Override
    public int getSpreadNumber(){ return this.spreadNumber; }

    @Override
    public void setName() {
        if(this.LongOrShort().equals(Belfort.BUY)){
            super.name = "Long Strangle";
        }
        else{
            super.name = "Short Strangle";
        }
    }

    @Override
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        List <Double> strangle = new ArrayList<>();
        OneSymmetricalSpreadStrategy alfa = new OneSymmetricalSpreadStrategy();
        strangle = alfa.AllPrices(price, spreads, o);
        return strangle;
    }

    @Override
    public List <OptionLeg> setOptionLegs(List<Double> prices){
        List <OptionLeg> legs = new ArrayList<>();
        if(this.LongOrShort().equals(Belfort.BUY)){
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.BUY));
            legs.add(new OptionLeg(prices.get(1), OptionType.CALL, Belfort.BUY));
        }
        else{
            legs.add(new OptionLeg(prices.get(0), OptionType.PUT, Belfort.SELL));
            legs.add(new OptionLeg(prices.get(1), OptionType.CALL, Belfort.SELL));
        }
        return legs;
    }
}

