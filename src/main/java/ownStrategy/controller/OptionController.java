package ownStrategy.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.dto.*;
import ownStrategy.logic.finance.OptionCalculator;
import ownStrategy.logic.network.TickerSearch;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.OptionStrategy;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.logic.sPattern.SpreadStrategy;
import ownStrategy.model.TheWallet;
import ownStrategy.service.OptionService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OptionController {
    private final OptionService service;
    public OptionController(OptionService service) {
        this.service = service;
    }
///fasada
    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> preview(@RequestBody TradingDTO trade){
        trade.getStrategy().setName();
        service.checkQuant(trade.getQuant());//H
        service.checkType(trade.getOptionType(), trade.getStrategy());//H
        service.setType(trade.getOptionType(), trade.getStrategy());
        trade.getStrategy().setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), trade.getExpiry()) / 365.0);
        double price = service.getStockPrice(trade.getTicker());
        service.validateSpreads(trade.getSpreads(), trade.getStrategy());//H
        List<OptionLeg> legs = service.calculateLegs(trade.getStrategy(), price, trade.getSpreads());
        List<ChartPoint> points = service.chart(trade.getStrategy(), legs, price, trade.getQuant());
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

/*    @PostMapping("/strategy/{pos}/{type}/{quant}/{key}/{optionType}")
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
            Belfort position = service.belfort(pos);
            SpreadStrategy strategy = service.requested(type, position);
            service.checkQuant(quant);
            List<CompanyDTO> companies = tickerSearch.Companies(key);
//        String selection = "no i co z tym stringiem";
            int choice = service.getChoice(selection);
            String ticker = companies.get(choice - 1).getTicker();
            service.setType(optionType, strategy);
            strategy.setTimeToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), request.getExpiry()) / 365.0);
            double price = service.getStockPrice(ticker);
            price = 220.0;
            service.validateSpreads(request.getSpreads());
            List<OptionLeg> legs = service.calculateLegs(strategy, price, request.getSpreads());
            List<ChartPoint> points = service.chart(strategy, legs, price, quant);
            Map<String, Object> response = new HashMap<>();
            response.put("strategyName", strategy.getName());
            response.put("chartPoints", points);
        strategy.setName();

        return response;
    }
*/