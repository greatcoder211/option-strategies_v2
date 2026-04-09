package ownStrategy.logic.sPattern;

import ownStrategy.logic.finance.BlackScholes;

import java.util.List;

public class OptionStrategy {
    protected List<OptionLeg> legs;
    private double timeToExpiry = 0.08219178082;
    private  double riskFreeRate = 0.05;
    private  double volatility = 0.30;

    public OptionStrategy(){}

    public List<OptionLeg> getLegs() {
        return legs;
    }
    public  void setLegs(List<OptionLeg> legs) {
        this.legs = legs;
    }

    public void setTimeToExpiry(double timeToExpiry) {this.timeToExpiry = timeToExpiry;}

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }


    public double getTimeToExpiry() { return timeToExpiry;}

    public double getRiskFreeRate(){ return  riskFreeRate;}

    public double getVolatility(){ return  volatility;}

    public double netPremium(List<OptionLeg> legs, double price) {
        double res = 0;
        for(OptionLeg leg : legs){
            if(leg.getBelfort().equals(Belfort.SELL) && leg.getType().equals(OptionType.CALL)){
                res += BlackScholes.calculateCallPrice(price, leg.getStrikePrice(), timeToExpiry, riskFreeRate, volatility);
            }
            else if(leg.getBelfort().equals(Belfort.SELL) && leg.getType().equals(OptionType.PUT)){
                res += BlackScholes.calculatePutPrice(price, leg.getStrikePrice(), timeToExpiry, riskFreeRate, volatility);
            }
            else if(leg.getBelfort().equals(Belfort.BUY) && leg.getType().equals(OptionType.CALL)){
                res -= BlackScholes.calculateCallPrice(price, leg.getStrikePrice(), timeToExpiry, riskFreeRate, volatility);
            }
            else if(leg.getBelfort().equals(Belfort.BUY) && leg.getType().equals(OptionType.PUT)){
                res -= BlackScholes.calculatePutPrice(price, leg.getStrikePrice(), timeToExpiry, riskFreeRate, volatility);
            }
        }
        return res;
    }
    //mnozniki kierunku- w matematyce finansowej mnozymy razy 1 lub -1 faktycznie trochę szybsze ale bez przesady
    public double calculateProfits(List<OptionLeg> legs, double price2) {
        double res = 0;
        for(OptionLeg leg : legs){
            if(leg.getBelfort().equals(Belfort.BUY)){
                if(leg.getType().equals(OptionType.CALL) && leg.getStrikePrice() < price2){
                    res += price2 - leg.getStrikePrice();
                }
                if(leg.getType().equals(OptionType.PUT) && leg.getStrikePrice() > price2){
                    res += leg.getStrikePrice() - price2;
                }
            }
            else if(leg.getBelfort().equals(Belfort.SELL)){
                if(leg.getType().equals(OptionType.CALL) && leg.getStrikePrice() < price2){
                    res -= price2 - leg.getStrikePrice();
                }
                if(leg.getType().equals(OptionType.PUT) && leg.getStrikePrice() > price2){
                    res -= leg.getStrikePrice() - price2;
                }
            }
        }
        return res;
    }
}
