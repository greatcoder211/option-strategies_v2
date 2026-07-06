package ownStrategy.model.strategy;
import lombok.Data;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.logic.finance.BlackScholes;
import ownStrategy.model.Belfort;
import ownStrategy.model.PricingContext;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Data
public abstract class OptionStrategy {
    public List<OptionLeg> optionLegs;
    public abstract List<OptionLeg> generateLegs(double spotPrice);
    //jak chcesz zrobić wariant opłacalności danej opcji na danym przedziale(nie: od dzisiaj), to trzeba podać parametr tego dnia "od kiedy" do metody
    public double calculateNetPremium(double spotPrice, PricingContext pricingContext){
        double res = 0;
        for(OptionLeg leg : optionLegs){
            if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.CALL)){
                res += BlackScholes.calculateCallPrice(spotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.SELL) && leg.type().equals(OptionType.PUT)){
                res += BlackScholes.calculatePutPrice(spotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.CALL)){
                res -= BlackScholes.calculateCallPrice(spotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            else if(leg.position().equals(Belfort.BUY) && leg.type().equals(OptionType.PUT)){
                res -= BlackScholes.calculatePutPrice(spotPrice, leg.strikePrice(), ChronoUnit.DAYS.between(leg.tradeDate(), leg.expiryDate()) / 365.0, pricingContext.riskFreeRate(), pricingContext.volatility());
            }
            res *= leg.strikePrice();
        }
        return res;
    }
    //mnozniki kierunku- w matematyce finansowej mnozymy razy 1 lub -1 faktycznie trochę szybsze ale bez przesady
    public double calculatePayoff(double spotPrice) {
        double res = 0;
        for(OptionLeg leg : optionLegs){
            if(leg.position().equals(Belfort.BUY)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < spotPrice){
                    res += spotPrice - leg.strikePrice();
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > spotPrice){
                    res += leg.strikePrice() - spotPrice;
                }
            }
            else if(leg.position().equals(Belfort.SELL)){
                if(leg.type().equals(OptionType.CALL) && leg.strikePrice() < spotPrice){
                    res -= spotPrice - leg.strikePrice();
                }
                if(leg.type().equals(OptionType.PUT) && leg.strikePrice() > spotPrice){
                    res -= leg.strikePrice() - spotPrice;
                }
            }
            res *= leg.quantity();
        }
        return res;
    }
    public double calculatePnL(double spotPrice, PricingContext pricingContext){
        return calculatePayoff(spotPrice) - calculateNetPremium(spotPrice, pricingContext);
    }
}