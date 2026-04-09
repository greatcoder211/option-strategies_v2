package ownStrategy.logic.sPattern;

public class OptionLeg {
    private double strikePrice;
    private OptionType type;
    private Belfort belfort;

    public OptionLeg(double strikePrice, OptionType type,  Belfort belfort) {
        this.strikePrice = strikePrice;
        this.type = type;
        this.belfort = belfort;
    }

    public double getStrikePrice() { return strikePrice; }

    public OptionType getType() { return type; }

    public Belfort getBelfort() { return belfort; }

    @Override
    public String toString() { return belfort + " " + type + " @" + strikePrice; }
}
