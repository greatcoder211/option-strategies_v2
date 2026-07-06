package ownStrategy.model.strategy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.logic.finance.BlackScholesUtils;
import ownStrategy.model.OptionLeg;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
import ownStrategy.model.PricingContext;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Data
public abstract class OptionStrategy {
    @NotNull
    @Min(1)
    protected final int quantity;
    @NotNull
    protected final Belfort position;
    protected String strategyName;
    @NotNull
    protected List<OptionLeg> optionLegs;

    public OptionStrategy(int quantity, Belfort position) {
        this.quantity = quantity;
        this.position = position;
    }

    public abstract List<OptionLeg> generateLegs(double spotPrice);

    public abstract void validateData(double spotPrice);

    public abstract List<ChartPoint> calculatePreviewChart(Request request);

    public double calculateNetPremium(double entrySpotPrice, PricingContext pricingContext){
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
    //mnozniki kierunku- w matematyce finansowej mnozymy razy 1 lub -1 faktycznie trochę szybsze ale bez przesady
    //tam: "entrySpotPrice", a tutaj: "simulatingSpotPrice", no logiczne, czyż nie?
    public double calculatePayoff(double simulatedSpotPrice) {
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

    public double calculatePnL(double entrySpotPrice, double simulatedSpotPrice, PricingContext pricingContext){
        return calculatePayoff(simulatedSpotPrice) - calculateNetPremium(entrySpotPrice, pricingContext);
    }
}