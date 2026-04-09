package ownStrategy.legacy.oPattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ownStrategy.logic.finance.BlackScholes;
import ownStrategy.logic.finance.OptionCalculator;
import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.SpreadStrategy;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StrategyCalculator implements Observer {
    private double gamePrice;
    private double currentPrice;
    private int quantity;
    private List<OptionLeg> legs = new ArrayList<OptionLeg>();
    private PriceWatcher watcher;
    ObjectMapper mapper = new ObjectMapper();
    public void setLegs(List<OptionLeg> legs) {
        this.legs = legs;
    }
    public StrategyCalculator(PriceWatcher watcher) {
        this.watcher = watcher;
    }

    public double getGamePrice() {
        return gamePrice;
    }
    public void setGamePrice(double gamePrice) {
        this.gamePrice = gamePrice;
    }
    public double getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public List<OptionLeg> getLegs() {
        return legs;
    }

    public int jsonSize(){
        try{
            JsonNode root = mapper.readValue(new File("data/game.json"),  JsonNode.class);
            return root.size();
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void update() {
        try{
            JsonNode root = mapper.readValue(new File("data/game.json"),  JsonNode.class);
            gamePrice = root.path("price").asDouble();
            currentPrice = watcher.getCurrentPrice();
            String name = root.path("name").asText();
            Belfort belf = Belfort.valueOf(root.path("belfort").asText());
            String fullClassName = "ownStrategy.sPattern." + name.replace(" ", "");
            Class<?> clazz = Class.forName(fullClassName);
            SpreadStrategy strategy = (SpreadStrategy) clazz
                    .getDeclaredConstructor(String.class, Belfort.class)
                    .newInstance(name, belf);
            List<Double> spreads = new ArrayList<>();
            for (JsonNode spread : root.path("spreads")) {
                spreads.add(spread.asDouble());
            }
            legs = strategy.setOptionLegs(strategy.setThePrices(gamePrice, spreads, strategy));
            JsonNode nodes = root.path("expiration");
            LocalDate expiry = LocalDate.of(
                    nodes.get(0).asInt(),
                    nodes.get(1).asInt(),
                    nodes.get(2).asInt()
            );
            strategy.setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), expiry) / 365.0);
            quantity = root.path("quantity").asInt();
            strategy.setLegs(legs);
            double f = OptionCalculator.function(strategy, gamePrice, currentPrice, quantity);
            for(OptionLeg leg : legs) {
                System.out.println("Leg Strike: " + leg.getStrikePrice() + " Type: " + leg.getType() + " Current Call Price: " + BlackScholes.calculateCallPrice(currentPrice, leg.getStrikePrice(), ChronoUnit.DAYS.between(LocalDate.now(), expiry) / 365.0, 0.05, 0.3));
            }
            System.out.println("The price was " + gamePrice + " and now it is " + currentPrice);
            if(f > 0){
                System.out.println("Profit: " + f);
            }
            else if(f == 0){
                System.out.println("You are even!");
            }
            else{
                System.out.println("Loss: " + f);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
