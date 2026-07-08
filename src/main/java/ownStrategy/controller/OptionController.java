package ownStrategy.controller;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.*;
import ownStrategy.dto.portfolio.PortfolioStrategy;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.dto.strategyPanel.Trade;
import ownStrategy.logic.finance.StrategyCalculator;
import ownStrategy.model.OptionLeg;
import ownStrategy.model.TheWallet;
import ownStrategy.service.OptionService;
import ownStrategy.service.PreviewService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OptionController {
    //solange es keinen Service mehr gibt, kann es so sein, sonst- muss es sich aendern
    private final OptionService optionService;
    private final PreviewService previewService;
    public OptionController(OptionService optionService, PreviewService previewService) {
        this.optionService = optionService;
        this.previewService = previewService;
    }
    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> preview(@RequestBody Request request){
        /*
                    List<CompanyDTO> companies = tickerSearch.Companies(key);
//        String selection = "no i co z tym stringiem";
            int choice = service.getChoice(selection);
            String ticker = companies.get(choice - 1).getTicker();
        */
        Map<String, Object> response = new HashMap<>();
        response.put("strategyName", request.getStrategyName());
        response.put("chartPoints", previewService.processPreviewChart(request));
        return response;
    }

    @PostMapping("/execute")
    @ResponseBody
    public Map<String, Object> executeStrategy(@RequestBody Request request){
        Map<String, Object> response = new HashMap<>();
        response.put("strategy", optionService.createStrategy(request));
        return response;
    }

    @GetMapping("/portfolio/{userID}")
    public List<PortfolioStrategy> getUserStrategies(@PathVariable String userID){
        return optionService.getUserStrategies(userID);
    }

    @PostMapping("/portfolio/score")
    @ResponseBody
    public double checkPnL(@RequestBody PortfolioStrategy portfolioStrategy){
        //choć chciałem to bardzo zrobić z pominięciem serwisu, to nie da się, bo muszę skorzystać z MarketClienta
        return optionService.calculatePnL(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice(), portfolioStrategy.getCompany().ticker());
    }

    @PostMapping("/portfolio/chart")
    @ResponseBody
    //po prostu wykres strategii wraz z punktem, w którym jesteśmy
    public Map<List<ChartPoint>, ChartPoint> scoreChart(@RequestBody PortfolioStrategy portfolioStrategy){
        List<ChartPoint> strategyChart = optionService.makeChart(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice());
        ChartPoint currentPriceMarker = optionService.makeCurrentPriceMarker(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice(), portfolioStrategy.getCompany().ticker());
        return Map.of(strategyChart, currentPriceMarker);
    }

    @GetMapping("/portfolio/filter")
    public Page<PortfolioStrategy> getFilteredTrades(Filter filter) {
        return optionService.filter(filterDto);
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