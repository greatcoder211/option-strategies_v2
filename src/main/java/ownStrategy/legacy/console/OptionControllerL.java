package ownStrategy.legacy.console;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ownStrategy.legacy.oPattern.PriceWatcher;
import ownStrategy.logic.OptionRepositoryL;
import ownStrategy.logic.sPattern.*;
import ownStrategy.legacy.oPattern.Game;
import ownStrategy.legacy.oPattern.StrategyCalculator;
import ownStrategy.dto.ChartPoint;
import ownStrategy.logic.network.SimpleHttpServer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class OptionControllerL {
    private final OptionServiceL service;
    private final OptionRepositoryL repository;
    private final OptionUIL ui;


    public OptionControllerL(OptionServiceL service, OptionRepositoryL repository, OptionUIL ui) {
        this.service = service;
        this.repository = repository;
        this.ui = ui;
    }

    public void start() {
        ui.print2("You want to check the price on your current position?\n1.Yes\n2.No");
        if (ui.getInt() == 1) {
            try {
                JsonNode root = repository.loadGameJson();
                service.currentPosition(root.path("ticker").asText(), root.path("price").asDouble());
            } catch (Exception e) { e.printStackTrace(); }
        }

        ui.print2("Choose the number of your strategy:  \n1. Butterfly Spread \n2. Vertical Spread \n3. Ratio Spread \n4. Iron Condor \n5. Iron Butterfly \n6. Strangle");
        int n = 0;
        while (n < 1 || n > StrategyType.values().length) n = ui.getInt();

        ui.print2("You want to play Long or short?\n1. Long\n2. Short");
        int lsh = 0;
        while (lsh < 1 || lsh > 2) lsh = ui.getInt();
        ui.getNextLine("");

        ui.print("How much are you willing to ");
        if(lsh == 1){
            ui.print2("buy?");
        }
        else if(lsh == 2){
            ui.print2("sell?");
        }
        int quantity = ui.getInt();
        ui.getNextLine("");

        SpreadStrategy os = service.createOptionStrategy(StrategyType.values()[n - 1], Belfort.values()[lsh - 1]);

        String ticker = ui.getTicker();
        double price = service.getStockPrice(ticker);
        while (price == -1) {
            ui.print2("Enter the right value!");
            ticker = ui.getTicker();
            price = service.getStockPrice(ticker);
        }
        ui.print2("Price: " + price + " USD");

        // Sekcja Spreadów
        List<Double> spreadValues = new ArrayList<>();
        if (os.getSpreadNumber() == 1) ui.print2("Enter the spread: ");
        else if (os.getSpreadNumber() == 2) ui.print2("Enter the lower spread: ");

        double spread = getValidSpread(price);
        spreadValues.add(spread);
        os.setSpread2(spread);

        if (os.getSpreadNumber() == 2) {
            ui.print2("Enter the higher spread: ");
            spread = getValidSpread(price);
            spreadValues.add(spread);
        }

        // Call/Put
        if (os.getCP()) {
            int cpu = 0;
            while (cpu < 1 || cpu > 2) {
                ui.print2("You want to play on:\n1. CALL\n2. PUT");
                cpu = ui.getInt();
                os.setType(cpu == 1 ? OptionType.CALL : OptionType.PUT);
            }
        }

        // Data
        ui.getNextLine("Enter the expiration date: ");
        LocalDate expiry = getValidDate();
        os.setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), expiry) / 365.0);
        os.setName();

        // Generowanie nóg
        List<OptionLeg> legs = service.calculateLegs(os, price, spreadValues);
        os.setLegs(legs);
        for (OptionLeg ol : legs) ui.print2(ol.toString());

        // Gra (Gambler mode)
        StrategyCalculator calc = new StrategyCalculator(new PriceWatcher(ticker));
        if (calc.jsonSize() != -1) {
            ui.print2("Do you want to play?\n 1. Yes\n 2. No");
            if (ui.getInt() == 1) {
                ui.print2("Are you sure? You will delete your current position!\n1.Yes\n2.No");
                if (ui.getInt() == 1) {
                    ui.print2("Gambler mode on.");
                    Game game = new Game(ticker, quantity, Belfort.values()[lsh - 1], os.getName(), price, spreadValues, expiry);
                    repository.saveGame(game);
                    calc.setLegs(legs);
                } else ui.print2("No changes made then.");
            } else ui.print2("No risk no ferrari.");
        }

        // Wykres
        ui.print2("You want me to draw it?\\n1.Yes\\n2.No\"");
        if (ui.getInt() == 1) {
            ui.getNextLine(""); // sc.nextLine()
            drawChart(os, legs, price, quantity);
        } else ui.print2("I won't draw it then.");
    }

    private double getValidSpread(double price) {
        while (true) {
            double s = ui.getDouble();
            if (s > price || s < 0) ui.print2("Enter the right value!");
            else return s;
        }
    }

    private LocalDate getValidDate() {
        while (true) {
            try {
                String[] data = ui.getNextLine("").split(" ");
                LocalDate d = LocalDate.of(Integer.parseInt(data[2]), Integer.parseInt(data[1]), Integer.parseInt(data[0]));
                if (d.isBefore(LocalDate.now()) || d.isAfter(LocalDate.now().plusYears(3)) || d.isEqual(LocalDate.now())) {
                    ui.print2("Please enter a valid date!");
                    continue;
                }
                return d;
            } catch (Exception e) { ui.print2("Please enter a valid date!"); }
        }
    }

    private void drawChart(SpreadStrategy os, List<OptionLeg> legs, double price, int quantity) {
        try {
            ui.print2("Generating chartpoints...");
            os.setLegs(legs);
            List<ChartPoint> points = Chart.draw(os, legs, price, quantity);
            Map<String, Object> dataPacket = new HashMap<>();
            dataPacket.put("strategyName", os.getName());
            dataPacket.put("chartPoints", points);
            String json = new ObjectMapper().writeValueAsString(dataPacket);
            SimpleHttpServer.startServer(json);
            ui.getNextLine("Press ENTER to exit...");
            ui.print2("Shutting down server...");
            System.exit(0);
        } catch (Exception e) { e.printStackTrace(); }
    }
}