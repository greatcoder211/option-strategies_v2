package ownStrategy.logic.finance;

import ownStrategy.model.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.PricingContext;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

//quasi-"klasa narzędziowa" do wyliczania Option utils
public class StrategyCalculator {

    public static double calculateLegNetPremium(OptionLeg leg, double entrySpotPrice, PricingContext pricingContext) {
        double netPremium = 0;
        double legIntrinsicValue = 0;
        if (leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.CALL)) {
            legIntrinsicValue = BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(pricingContext.evaluationDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
        } else if (leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.PUT)) {
            legIntrinsicValue = BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(pricingContext.evaluationDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
        } else if (leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.CALL)) {
            legIntrinsicValue = -1 * BlackScholesUtils.calculateCallPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(pricingContext.evaluationDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
        } else if (leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.PUT)) {
            legIntrinsicValue = -1 * BlackScholesUtils.calculatePutPrice(entrySpotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(pricingContext.evaluationDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
        }
        netPremium += (legIntrinsicValue * leg.quantity());
        return netPremium;
    }

    public static double calculateLegPayoff(OptionLeg leg, double simulatedSpotPrice) {
        double legPayoff = 0;
        if (leg.position().equals(Belfort.BUY)) {
            if (leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice) {
                legPayoff = simulatedSpotPrice - leg.strikePrice();
            }
            if (leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice) {
                legPayoff = leg.strikePrice() - simulatedSpotPrice;
            }
        } else if (leg.position().equals(Belfort.SELL)) {
            if (leg.type().equals(OptionType.CALL) && leg.strikePrice() < simulatedSpotPrice) {
                legPayoff = -1 * (simulatedSpotPrice - leg.strikePrice());
            }
            if (leg.type().equals(OptionType.PUT) && leg.strikePrice() > simulatedSpotPrice) {
                legPayoff = -1 * (leg.strikePrice() - simulatedSpotPrice);
            }
        }
        return legPayoff * leg.quantity();
    }

    public static double calculatePnL(List<OptionLeg> optionLegs, double entrySpotPrice, double simulatedSpotPrice, PricingContext pricingContext) {
        double totalPnL = 0;
        //wyliczenie na początku
        for (OptionLeg leg : optionLegs) {
            totalPnL += calculateLegNetPremium(leg, entrySpotPrice, new PricingContext(pricingContext.riskFreeRate(), pricingContext.volatility(), leg.tradeDate()));
        }

        for (OptionLeg leg : optionLegs) {
            if (!leg.expiryDate().isAfter(pricingContext.evaluationDate())) {
                totalPnL += calculateLegPayoff(leg, simulatedSpotPrice);
            } else
                totalPnL -= calculateLegNetPremium(leg, simulatedSpotPrice, pricingContext);
        }
        return totalPnL;
//            return calculateLegPayoff(optionLegs, simulatedSpotPrice, pricingContext) + calculateLegNetPremium(optionLegs, entrySpotPrice, pricingContext);
    }
}
