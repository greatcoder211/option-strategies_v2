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
        double netPremium = 0;
        for(OptionLeg leg : optionLegs){
            double legIntrinsicValue = 0;
            if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.CALL)){
                legIntrinsicValue = BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.PUT)){
                legIntrinsicValue = BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.CALL)){
                legIntrinsicValue = -1 * BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.PUT)){
                legIntrinsicValue = -1 * BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            netPremium += (legIntrinsicValue * leg.quantity());
        }
        return netPremium;
    }

    public static double calculatePayoff(List<OptionLeg> optionLegs, double simulatedSpotPrice) {
        double res = 0;
        for(OptionLeg leg : optionLegs){
            double legPayoff = 0;
            if(leg.position().equals(Belfort.BUY)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice){
                    legPayoff = simulatedSpotPrice - leg.strikePrice();
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice){
                    legPayoff = leg.strikePrice() - simulatedSpotPrice;
                }
            }
            else if(leg.position().equals(Belfort.SELL)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice){
                    legPayoff = -1 * (simulatedSpotPrice - leg.strikePrice());
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice){
                    legPayoff = -1 * (leg.strikePrice() - simulatedSpotPrice);
                }
            }
            res += legPayoff * leg.quantity();
        }
        return res;
    }

    public static double calculatePnL(List<OptionLeg> optionLegs, double entrySpotPrice, double simulatedSpotPrice, PricingContext pricingContext){
        return calculatePayoff(optionLegs, simulatedSpotPrice) + calculateNetPremium(optionLegs, entrySpotPrice, pricingContext);
    }
}
