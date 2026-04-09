package ownStrategy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ownStrategy.dto.ChartPoint;
import ownStrategy.exceptions.QuantityException;
import ownStrategy.exceptions.SpreadException;
import ownStrategy.legacy.console.Chart;
import ownStrategy.logic.network.AlphaVantageStock;
import ownStrategy.logic.network.SimpleHttpServer;
import ownStrategy.logic.sPattern.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpringService {
    public SpreadStrategy requested(String type, Belfort belfort) {
        String formattedType = Arrays.stream(type.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining("_"));
        try {
            Class<? extends SpreadStrategy> clazz = StrategyType.valueOf(formattedType).getStrategyClass();
            return clazz.getConstructor(String.class, Belfort.class)
                    .newInstance(formattedType.replace("_", " "), belfort);

        } catch (Exception e) {
            throw new RuntimeException("We couldn't manage to initialize your strategy: " + type);
        }
    }
    public Belfort belfort(String pos){
        if(pos.equals("BUY")){
            return Belfort.BUY;
        }
        else if(pos.equals("SELL")){
            return Belfort.SELL;
        }
        else {
            throw new RuntimeException("You either BUY or SELL. You cannot do it other way." + pos);
        }
    }
    public void setType(OptionType type, SpreadStrategy strategy) {
        if (type == null) return;
        if(type.equals(OptionType.CALL)){
            strategy.setType(OptionType.CALL);
        }
        else if(type.equals(OptionType.PUT)){
            strategy.setType(OptionType.PUT);
        }
    }
    public List<OptionLeg> calculateLegs(SpreadStrategy os, double price, List<Double> spreadValues) {
        List<Double> prices = os.setThePrices(price, spreadValues, os);
        for (int i = 0; i < prices.size(); i++) {
            prices.set(i, Math.round(prices.get(i) * 100.0) / 100.0);
        }
        os.setLegs(os.setOptionLegs(prices));
        return os.setOptionLegs(prices);
    }

    public double getStockPrice(String ticker) {
        return AlphaVantageStock.getPrice(ticker);
    }

    public List<ChartPoint> chart(SpreadStrategy strategy, List<OptionLeg> legs, double price, int quantity) {
        List<ChartPoint> points = new ArrayList<>();
        try {
            points = Chart.draw(strategy, legs, price, quantity);
        } catch (Exception e) { e.printStackTrace();}
        return points;
    }

    public int getChoice(String line){
        try {
            if(line.charAt(0) <= '9'){
                return Character.getNumericValue(line.charAt(0));
            }
            else{
                return Integer.parseInt(line.split(" ")[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse index from: " + line);
        }
    }
    public void validateSpreads(List<Double> spreads) {
        String message = "Invalid spreads. Only positive numerical values.";
        if (spreads == null || spreads.isEmpty()) {
            throw new SpreadException(message);
        }
        for (Double s : spreads) {
            if (s == null) {
                throw new SpreadException(message);
            }
        }
    }
    public void checkQuant(int quant){
        if(quant < 1){
            throw new QuantityException();
        }
    }
}
