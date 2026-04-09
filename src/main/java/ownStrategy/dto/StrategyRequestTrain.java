package ownStrategy.dto;

import ownStrategy.logic.sPattern.OptionLeg;

import java.util.List;

import jakarta.validation.constraints.*;

public class StrategyRequestTrain {
    @NotEmpty(message = "you have to add at least one strategy leg")
    private List<OptionLeg> legs;

    @Positive(message = "the spot price needs to be positive")
    private double spotPrice;


    private double timeToExpiry = 0.08219178082;

    @PositiveOrZero(message = "volatility cannot be negative")
    @DecimalMax(value = "1.0", message = "max volatility is 1.0")
    private double volatility = 0.30;

    private double riskFreeRate = 0.05;

    public StrategyRequestTrain(List<OptionLeg> legs, double spotPrice) {
        this.legs = legs;
        this.spotPrice = spotPrice;
    }

    public StrategyRequestTrain(){}

    public List<OptionLeg> getLegs() {
        return legs;
    }
    public double getSpotPrice() {
        return spotPrice;
    }
    public double getTimeToExpiry() {
        return timeToExpiry;
    }
    public double getVolatility() {
        return volatility;
    }
    public double getRiskFreeRate() {
        return riskFreeRate;
    }
    public void setLegs(List<OptionLeg> legs) {
        this.legs = legs;
    }
    public void setSpotPrice(double spotPrice) {
        this.spotPrice = spotPrice;
    }
    public void setTimeToExpiry(double timeToExpiry) {
        this.timeToExpiry = timeToExpiry;
    }
    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }
    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

}
