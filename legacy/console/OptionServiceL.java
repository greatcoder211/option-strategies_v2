package ownStrategy.legacy.console;

import org.springframework.stereotype.Service;
import ownStrategy.logic.network.AlphaVantageStock;
import ownStrategy.legacy.oPattern.PriceWatcher;
import ownStrategy.legacy.oPattern.StrategyCalculator;
import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.SpreadStrategy;
import ownStrategy.logic.sPattern.StrategyType;

import java.lang.reflect.Constructor;
import java.util.List;

@Service
public class OptionServiceL {

    public void currentPosition(String ticker, double last) {
        PriceWatcher p = new PriceWatcher(ticker);
        p.setLast(last);
        p.registerObserver(new StrategyCalculator(p));
        p.checkPrice();
    }

    public SpreadStrategy createOptionStrategy(StrategyType type, Belfort belf) {
        try {
            Class<? extends SpreadStrategy> clazz = type.getStrategyClass();
            String prefix = (belf == Belfort.BUY) ? "Long" : "Short";
            String niceName = prefix + " " + type.name().replace("_", " ");
            Constructor<? extends SpreadStrategy> constructor = clazz.getConstructor(String.class, Belfort.class);
            return constructor.newInstance(niceName, belf);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("We didn't manage to create a strategy named: " + type);
        }
    }

    public double getStockPrice(String ticker) {
        return AlphaVantageStock.getPrice(ticker);
    }

    public List<OptionLeg> calculateLegs(SpreadStrategy os, double price, List<Double> spreadValues) {
        List<Double> prices = os.setThePrices(price, spreadValues, os);
        for (int i = 0; i < prices.size(); i++) {
            prices.set(i, Math.round(prices.get(i) * 100.0) / 100.0);
        }
        return os.setOptionLegs(prices);
    }
}