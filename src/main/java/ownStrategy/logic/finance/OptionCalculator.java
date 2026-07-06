package ownStrategy.logic.finance;
import lombok.NoArgsConstructor;
import ownStrategy.config.DefaultPricingContext;
import ownStrategy.model.PricingContext;
import ownStrategy.model.strategy.OptionStrategy;
@NoArgsConstructor(force = true)
public class OptionCalculator {
    //we are going through the "stimulated" prices one by one to calculate PnL at specific points
    private final DefaultPricingContext defaultPricingContext;
    public OptionCalculator(DefaultPricingContext defaultPricingContext) {
        this.defaultPricingContext = defaultPricingContext;
    }
    public double function(double entrySpotPrice, double simulatedSpotPrice, OptionStrategy optionStrategy) {
        return optionStrategy.calculatePnL(entrySpotPrice, simulatedSpotPrice, new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility()));
    }
}
//bylo w pnl:       return optionStrategy.getQuantity() * (optionStrategy.calculateNetPremium(entrySpotPrice, new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility()))  + optionStrategy.calculatePayoff(simulatedSpotPrice));
//price- stockprice w momencie zakupu strategii
//price2- nowa cena(nowy stockprice)
