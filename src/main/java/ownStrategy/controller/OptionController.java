package ownStrategy.controller;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.*;
import ownStrategy.dto.strategyPanel.Trade;
import ownStrategy.model.OptionLeg;
import ownStrategy.model.TheWallet;
import ownStrategy.service.OptionService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OptionController {
    //solange es keinen Service mehr gibt, kann es so sein, sonst- muss es sich aendern
    private final OptionService service;
    public OptionController(OptionService service) {
        this.service = service;
    }
    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> preview(@RequestBody Trade trade){
        Map<String, Object> response = new HashMap<>();
        response.put("strategyName", trade.getStrategy().getName());
        response.put("chartPoints", points);
        return response;
    }
    @PostMapping("/execute")
    @ResponseBody
    public TheWallet execute(@RequestBody TradingDTO trade){
        trade.getStrategy().setType(trade.getOptionType());
        trade.getStrategy().setName();
        service.checkType(trade.getOptionType(), trade.getStrategy());//H
        double price = service.getStockPrice(trade.getTicker());
        service.currentPriceCheck(price);
        trade.getStrategy().setPrice(price);
        trade.getStrategy().setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), trade.getExpiry()) / 365.0);
        service.validateSpreads(trade.getSpreads(), trade.getStrategy());
        trade.getStrategy().setSpreads(trade.getSpreads());//tylko dla ratio spread
        List<OptionLeg> legs = trade.getStrategy().setOptionLegs(trade.getStrategy().setThePrices(trade.getStrategy().getPrice(), trade.getSpreads(), trade.getStrategy()));
        trade.getStrategy().setLegs(legs);
        TheWallet wallet = new TheWallet(trade.getTicker(), trade.getStrategy().getName(), trade.getOptionType(), legs, trade.getExpiry());
        service.firstWalletCheck(wallet, trade.getStrategy());
        service.checkUser(trade.getUserID(), wallet);
        service.saveToRepo(wallet);
        wallet.setPrice(price);
        return wallet;
    }
    @GetMapping("/portfolio/{userID}")
    public List<TheWallet> getUserWallets(@PathVariable String userID){
        return service.getTheWallets(userID);
    }
    @PostMapping("/portfolio/score")
    @ResponseBody
    public double score(@RequestBody TheWallet wallet){
        double currentPrice = service.getStockPrice(wallet.getTicker());
        double gamePrice = wallet.getPrice();
        service.currentPriceCheck(currentPrice);
        OptionStrategy strategy = new OptionStrategy();
        strategy.setLegs(wallet.getLegs());
        strategy.setTimeToExpiry(ChronoUnit.DAYS.between(wallet.getDate(), wallet.getExpiry()) / 365.0);
        return service.revenues(strategy, currentPrice, gamePrice, wallet.getQuant());
    }
    @PostMapping("/portfolio/chart")
    @ResponseBody
    public Map<ChartPoint, List<ChartPoint>> scoreChart(@RequestBody TheWallet wallet){
//1. points part
        OptionStrategy strategy = new OptionStrategy();
        strategy.setLegs(wallet.getLegs());
        strategy.setTimeToExpiry(ChronoUnit.DAYS.between(wallet.getDate(), wallet.getExpiry())/365.0);
        List<ChartPoint> points = service.chart(strategy, strategy.getLegs(), wallet.getPrice(), wallet.getQuant());
//2. point part
        double currentPrice = service.getStockPrice(wallet.getTicker());
        double gamePrice = wallet.getPrice();
        service.currentPriceCheck(currentPrice);
        double profit = service.revenues(strategy, currentPrice, gamePrice, wallet.getQuant());
        ChartPoint point = new ChartPoint(currentPrice, profit);
        return Map.of(point, points);
    }
    @GetMapping("/portfolio/filter")
    public Page<TheWallet> getFilteredTrades(FilterDTO filterDto) {
        return service.filter(filterDto);
    }
    @Scheduled
    public void closeUpdates() {
        service.closePosition();
    }
    @PostMapping
    @ResponseBody
    public Map<ChartPoint, List<ChartPoint>> currentChart(@RequestBody TheWallet wallet){
//1. points part
        OptionStrategy strategy = new OptionStrategy();
        strategy.setLegs(wallet.getLegs());
        strategy.setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), wallet.getExpiry())/365.0);
        List<ChartPoint> points = service.chart(strategy, strategy.getLegs(), wallet.getPrice(), wallet.getQuant());
//2. point part
        double currentPrice = service.getStockPrice(wallet.getTicker());
        double gamePrice = wallet.getPrice();
        service.currentPriceCheck(currentPrice);
        double profit = service.revenues(strategy, currentPrice, gamePrice, wallet.getQuant());
        ChartPoint point = new ChartPoint(currentPrice, profit);
        return Map.of(point, points);
    }
}

/*
    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> preview(@RequestBody Trade trade){
        trade.getStrategy().setName();
        service.checkQuant(trade.getQuant());//H
        service.checkType(trade.getOptionType(), trade.getStrategy());//H
        service.setType(trade.getOptionType(), trade.getStrategy());
        trade.getStrategy().setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), trade.getExpiry()) / 365.0);
        double spotPrice = service.getStockPrice(trade.getTicker());
        service.validateSpreads(trade.getSpreads(), trade.getStrategy());//H
        List<OptionLeg> legs = service.calculateLegs(trade.getStrategy(), spotPrice, trade.getSpreads());
        List<ChartPoint> points = service.chart(trade.getStrategy(), spotPrice);
        Map<String, Object> response = new HashMap<>();
        response.put("strategyName", trade.getStrategy().getName());
        response.put("chartPoints", points);
        return response;
    }

    @PostMapping("/strategy/{pos}/{type}/{quant}/{key}/{optionType}")
    @ResponseBody
    public Map<String, Object> major(@PathVariable String pos,
                                     @PathVariable String type,
                                     @Min(1)
                                     @PathVariable int quant,
                                     @RequestParam String selection,
                                     @PathVariable String key,
                                     @PathVariable OptionType optionType,
                                     @RequestBody StrategyRequest request
                        ){
            Belfort position = service.position(pos);
            SpreadStrategy strategy = service.requested(type, position);
            service.checkQuant(quant);
            List<CompanyDTO> companies = tickerSearch.Companies(key);
//        String selection = "no i co z tym stringiem";
            int choice = service.getChoice(selection);
            String ticker = companies.get(choice - 1).getTicker();
            service.setType(optionType, strategy);
            strategy.setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), request.getExpiry()) / 365.0);
            double strikePrice = service.getStockPrice(ticker);
            strikePrice = 220.0;
            service.validateSpreads(request.getSpreads());
            List<OptionLeg> legs = service.calculateLegs(strategy, strikePrice, request.getSpreads());
            List<ChartPoint> points = service.chart(strategy, legs, strikePrice, quant);
            Map<String, Object> response = new HashMap<>();
            response.put("strategyName", strategy.getName());
            response.put("chartPoints", points);
        strategy.setName();

        return response;
    }
*/