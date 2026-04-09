package ownStrategy.dto;

import ownStrategy.logic.sPattern.OptionType;

public class OptionPrices {
    private OptionType type;
    private double stockPrice;
    private double optionPrice;
    private double strikePrice;
    public OptionPrices(OptionType type, double stockPrice, double optionPrice, double strikePrice) {
        this.type = type;
        this.stockPrice = stockPrice;
        this.optionPrice = optionPrice;
        this.strikePrice = strikePrice;
    }
    public OptionType getType() {
        return type;
    }
    public void setType(OptionType type) {
        this.type = type;
    }
    public double getStockPrice() {
        return stockPrice;
    }
    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }
    public double getOptionPrice() {
        return optionPrice;
    }
    public void setOptionPrice(double optionPrice) {
        this.optionPrice = optionPrice;
    }
    public double getStrikePrice() {
        return strikePrice;
    }
    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }
}
