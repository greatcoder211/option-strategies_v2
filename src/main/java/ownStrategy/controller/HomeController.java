package ownStrategy.controller;

import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.OptionPrices;
import ownStrategy.dto.StrategyRequestTrain;
import ownStrategy.service.HomeService;
import ownStrategy.logic.finance.BlackScholes;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.dto.ChartPoint;

import java.util.ArrayList;
import java.util.List;

//@RestController
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/home")
    public String home() {
        return "This is really working!";
    }

    @GetMapping("/power")
    public String calculatePower(@RequestParam int base, @RequestParam int exponent) {
        double result = Math.pow(base, exponent);
        return base + " do potęgi " + exponent + " wynosi: " + result;
    }

    @GetMapping("/call-payoff")
    public String call(@RequestParam double s,
    @RequestParam double k,
    @RequestParam(defaultValue = "0.08219178082") double t,
    @RequestParam(defaultValue = "0.30") double r,
    @RequestParam(defaultValue = "0.05") double sigma
    ) {
        double p = BlackScholes.calculateCallPrice(s, k, t, r,  sigma);
        double profit = Math.max(0, s - k) - p;
        return "Your revenue: " + profit;
    }

    @GetMapping("/call_put-payoff")
    public String put(@RequestParam double s,
    @RequestParam double k,
    @RequestParam(defaultValue = "0.08219178082") double t,
  @RequestParam(defaultValue = "0.30") double r,
                      @RequestParam(defaultValue = "0.05") double sigma
    ){
        double p = BlackScholes.calculatePutPrice(s, k, t, r,  sigma);
        double revenue = Math.max(0, k - s) - p;
        return "Your revenue on this put option: " + revenue;
    }

    @GetMapping("call_or_put_payoff")
    public OptionPrices prices(@RequestParam OptionType type,
                               @RequestParam double stock,
                               @RequestParam double option,
                               @RequestParam double strike){
        return new OptionPrices(type, stock, option, strike);
    }

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }
    @GetMapping("/prices")
    public OptionPrices prices2(@RequestParam OptionType type,
    @RequestParam double stock,
    @RequestParam double option,
    @RequestParam double strike){
        return homeService.createPricesReport(type, stock, option, strike);
    }

    @GetMapping("/calculation")
    public List<ChartPoint> points(@RequestParam OptionType type,
    @RequestParam double strike,
                                   @RequestParam double optionPrice,
                                   @RequestParam double minPrice,
                                   @RequestParam double maxPrice
    ){
        return homeService.calculatePayoffChart(type, strike, optionPrice, minPrice, maxPrice);
    }

    @PostMapping("/first")
    public List<ChartPoint> calculateFirst(
            @RequestBody StrategyRequestTrain request
            ){
        List <ChartPoint> points = new ArrayList<>();
        double minLeg = Double.MAX_VALUE;
        double maxLeg = Double.MIN_VALUE;
        for(
                OptionLeg leg : request.getLegs()){
            if(leg.getStrikePrice() > maxLeg){
                maxLeg = leg.getStrikePrice();
            }
            if(leg.getStrikePrice() < minLeg){
                minLeg = leg.getStrikePrice();
            }
        }
        int minValue =  (int) (0.8 * Math.floor(minLeg));
        int maxValue = (int) (1.2 * Math.floor(maxLeg));
        for(int x = minValue; x <= maxValue; x++){
            points.add(new ChartPoint(x, homeService.calculateStrategyPayoff(request, x)));
        }
        return points;
    }

}