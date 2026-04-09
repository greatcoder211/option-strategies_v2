package ownStrategy.legacy.oPattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ownStrategy.logic.sPattern.Belfort;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private String ticker;
    private int quantity;
    private Belfort belfort;
    private String name;
    private double price;
    private List<Double> spreads = new ArrayList<>();
    private LocalDate expiration;
    public Game(String ticker, int quantity, Belfort belfort, String name, double price, List<Double> spreads, LocalDate expiration) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.belfort = belfort;
        this.name = name;
        this.price = price;
        this.spreads = spreads;
        this.expiration = expiration;
    }
    public Game(){}

    public String getTicker() { return ticker;  }

    public void setTicker(String ticker) {  this.ticker = ticker;   }

    public Belfort getBelfort() {   return belfort;     }

    public String getName() {   return name;    }

    public double getPrice() {  return price;   }

    public List<Double> getSpreads() {  return spreads; }

    public LocalDate getExpiration() {  return expiration;  }

    public int getQuantity() {  return quantity; }

    public void setQuantity(int quantity) {  this.quantity = quantity;  }

    public void saveToJSon(){
        File file = new File("data/game.json");
        if(!file.getParentFile().exists() && file.getParentFile() != null){
            file.getParentFile().mkdirs();
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.findAndRegisterModules();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            if(!file.exists()){
                mapper.writeValue(file, this);
            }
            else{
                mapper.writeValue(file, this);
                JsonNode root = mapper.readTree(file);
                if(root.isArray()){
                    ((ArrayNode) root).remove(0);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
//git check