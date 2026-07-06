package ownStrategy.logic.oldStrategy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;

import java.util.List;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "typeID"
)
public abstract class SpreadStrategy extends OptionStrategy{
    protected String name;
    protected Belfort ls;
    SymmetricalSpreadStrategy spread;
    protected OptionType type;
    //glowna metoda- strategy pattern
    public List<Double> setThePrices(double price, List<Double> spreads, SpreadStrategy o) {
        return spread.AllPrices(price, spreads, o);
    }
    public List <OptionLeg> setOptionLegs(List<Double> prices) {
        return null;
    }
}

/*
        public SpreadStrategy(String name, Belfort ls) {
        this.name = name;
        this.ls = ls;
    }
    public SpreadStrategy(){}


    @Override
    public void setLegs(List<OptionLeg>  legs) {super.setLegs(legs);}

    public abstract int getSpreadNumber();

    public String getName() { return name; }

    public double getPrice() { return strikePrice; }

    public void setPrice(double strikePrice) { this.strikePrice = strikePrice; }

    public Belfort LongOrShort() { return ls; }

    public OptionType getType() { return type; }

    public void setType(OptionType type) { this.type = type; }

    public abstract boolean getCP();

    public double getSpread2() { return spread2; }

    public void setSpread2(double spread2) { this.spread2 = spread2; }

    public abstract void setName();

    public void setLs(Belfort ls) { this.ls = ls; }

    public double getTimeToExpiry() { return super.getTimeToExpiry(); }

    public double getRiskFreeRate(){ return super.getRiskFreeRate();}

    public double  getVolatility(){ return super.getVolatility();}

    public List<Double> getSpreads() { return spreads; }

    public void setSpreads(List<Double> spreads) { this.spreads = spreads; }
*/