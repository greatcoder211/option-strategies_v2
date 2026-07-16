package ownStrategy.service.strategy;

import org.springframework.stereotype.Service;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.model.OptionType;
import ownStrategy.model.Status;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.entity.portfolio.Request;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.mapper.StrategyFactoryRegistry;
import ownStrategy.logic.network.MarketDataClient;
import ownStrategy.logic.network.TickerSearch;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.OptionStrategy;

import java.time.LocalDate;
import java.util.List;

//Strategy Builder Service- two major methods are 'processPreviewChart' and 'createStrategy', everything pertains to the initial process of strategy generation
@Service
public class StrategyBuilderService {
    private final StrategyFactoryRegistry strategyFactoryRegistry;
    private final ChartGenerator chartGenerator;
    private final MarketDataClient marketDataClient;
    private final TickerSearch tickerSearch;
    public StrategyBuilderService(StrategyFactoryRegistry strategyFactoryRegistry, ChartGenerator chartGenerator, MarketDataClient marketDataClient, TickerSearch tickerSearch) {
        this.strategyFactoryRegistry = strategyFactoryRegistry;
        this.chartGenerator = chartGenerator;
        this.marketDataClient = marketDataClient;
        this.tickerSearch = tickerSearch;
    }

    public List<ChartPoint> processPreviewChart(Request request) {
           String ticker = getTicker(request.getKeySearch(), request.getSelectedCompany());
           double spotPrice = getSpotPrice(ticker);
           OptionStrategy domainStrategy = mapRequestToOptionStrategy(request);
           return chartGenerator.draw(spotPrice, domainStrategy.getOptionLegs());
    }

    public String getTicker(String keySearch, String selectedCompanyRaw){
        return getCompany(keySearch, selectedCompanyRaw).ticker();
    }

    public Company getCompany(String keySearch, String selectedCompanyRaw){
        List<Company> companies = tickerSearch.getCompanies(keySearch);
        int indexOfSelectedCompany = getIndexOfSelectedCompany(selectedCompanyRaw);
        return companies.get(indexOfSelectedCompany - 1);
    }

    public double getSpotPrice(String ticker){
        double price = marketDataClient.getStockPrice(ticker);
        if (price == -1) {
            throw new APILimitExceededException("Probably API limit exceeded. See you tomorrow!");
        }
        return price;
    }

    public OptionStrategy mapRequestToOptionStrategy(Request request){
        return strategyFactoryRegistry.mapToDomain(request);
    }

    public int getIndexOfSelectedCompany(String line){
        //line to są kolejne Stringi typu 1. Apple Inc. [AAPL] itd.
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

    public PortfolioStrategy createStrategy(Request request) {
        Company company = getCompany(request.getKeySearch(), request.getSelectedCompany());
        double spotPrice = getSpotPrice(company.ticker());
        OptionStrategy domainStrategy = mapRequestToOptionStrategy(request);
        return mapToPortfolio(domainStrategy, spotPrice, company);
    }

    public PortfolioStrategy mapToPortfolio(OptionStrategy domainStrategy, double spotPrice, Company company) {
        OptionType optionType = null;
        if (domainStrategy instanceof CallPutStrategy) {
            optionType = ((CallPutStrategy) domainStrategy).getOptionType();
        }
        Status strategyStatus;
        if(playNow(domainStrategy.getOptionLegs())){
            strategyStatus = Status.OPEN;
        }
        else{
            strategyStatus = Status.PENDING;
        }
        return PortfolioStrategy.builder()
                .quantity(domainStrategy.getQuantity())
                .position(domainStrategy.getPosition())
                .optionType(optionType)
                .strategyName(domainStrategy.getStrategyName())
                .company(company)
                .spotPrice(spotPrice)
                .optionLegs(domainStrategy.getOptionLegs())
                .status(strategyStatus)
                .build();
    }
    //jeśli choć jedna noga zaczyna grać od dzisiaj, to niech cała strategia stanie się teraźniejsza
    public boolean playNow(List<OptionLeg> optionLegs){
        for(OptionLeg optionLeg: optionLegs){
            if(optionLeg.tradeDate().equals(LocalDate.now())) return true;
        }
        return false;
    }
}