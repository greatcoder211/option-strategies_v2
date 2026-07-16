package ownStrategy.logic.finance;

import ownStrategy.model.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.PricingContext;

import java.time.temporal.ChronoUnit;
import java.util.List;
//quasi-"klasa narzędziowa" do wyliczania Option utils
public class StrategyCalculator {
    public static double calculateNetPremium(List<OptionLeg> optionLegs, double entrySpotPrice, PricingContext pricingContext) {
        double res = 0;
        for(OptionLeg leg : optionLegs){
            if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.CALL)){
                res += BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.PUT)){
                res += BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.CALL)){
                res -= BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.PUT)){
                res -= BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            res *= leg.quantity();
        }
        return res;
    }

    public static double calculatePayoff(List<OptionLeg> optionLegs, double simulatedSpotPrice) {
        double res = 0;
        for(OptionLeg leg : optionLegs){
            if(leg.position().equals(Belfort.BUY)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice){
                    res += simulatedSpotPrice - leg.strikePrice();
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice){
                    res += leg.strikePrice() - simulatedSpotPrice;
                }
            }
            else if(leg.position().equals(Belfort.SELL)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice){
                    res -= simulatedSpotPrice - leg.strikePrice();
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice){
                    res -= leg.strikePrice() - simulatedSpotPrice;
                }
            }
            res *= leg.quantity();
        }
        return res;
    }

    public static double calculatePnL(List<OptionLeg> optionLegs, double entrySpotPrice, double simulatedSpotPrice, PricingContext pricingContext){
        return calculatePayoff(optionLegs, simulatedSpotPrice) - calculateNetPremium(optionLegs, entrySpotPrice, pricingContext);
    }
}
