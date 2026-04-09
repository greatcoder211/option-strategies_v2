package ownStrategy.controller;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.CompanyDTO;
import ownStrategy.dto.StrategyRequest;
import ownStrategy.logic.network.TickerSearch;
import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.logic.sPattern.SpreadStrategy;
import ownStrategy.service.SpringService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
public class SpringController {
    private final TickerSearch tickerSearch;
    private final SpringService service;

    public SpringController(SpringService service, TickerSearch tickerSearch) {
        this.service = service;
        this.tickerSearch = tickerSearch;
    }

    @PostMapping("/strategy/{pos}/{type}/{quant}/{key}/{optionType}")
    @ResponseBody
    public Map<String, Object> major(@PathVariable String pos,
                                     @PathVariable String type,
                                     @Min(1) @PathVariable int quant,
                                     @RequestParam String selection,
                                     @PathVariable String key,
                                     @PathVariable OptionType optionType,
                                     @RequestBody StrategyRequest request) {
        Belfort position = service.belfort(pos);
        SpreadStrategy strategy = service.requested(type, position);
        service.checkQuant(quant);
        List<CompanyDTO> companies = tickerSearch.Companies(key);
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

    @GetMapping("/api/tickers")
    public List<String> companyList(@RequestParam String key) {
        List<CompanyDTO> companies = tickerSearch.Companies(key);
        List<String> companyList = new ArrayList<>();
        int i = 0;
        for (CompanyDTO company : companies) {
            i++;
            companyList.add(i + " - " + company.getName() + " (" + company.getTicker() + ")");
        }
        return companyList;
    }

    @GetMapping("/api/strategy-metadata/{type}")
    public Map<String, Object> getMetadata(@PathVariable String type) {
        SpreadStrategy strategy = service.requested(type, Belfort.BUY);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("spreadNumber", strategy.getSpreadNumber());
        metadata.put("isCP", strategy.getCP());
        return metadata;
    }
}