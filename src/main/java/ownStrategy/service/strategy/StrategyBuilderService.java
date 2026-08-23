package ownStrategy.service.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ownStrategy.config.DefaultPricingContext;
import ownStrategy.logic.network.CompanySearch;
import ownStrategy.model.PricingContext;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.model.OptionType;
import ownStrategy.model.Status;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.mapper.StrategyFactoryRegistry;
import ownStrategy.logic.network.PriceClient;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.model.entity.request.Request;
import ownStrategy.model.strategy.CallPutStrategy;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.repository.StrategyRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

//Strategy Builder Service- two major methods are 'processPreviewChart' and 'createStrategy', everything pertains to the initial process of strategy generation
@Service
public class StrategyBuilderService {

    private final StrategyRepository strategyRepository;
    private final StrategyFactoryRegistry strategyFactoryRegistry;
    private final ChartGenerator chartGenerator;
    private final PriceClient priceClient;
    private final CompanySearch companySearch;
    private final DefaultPricingContext defaultPricingContext;

    public StrategyBuilderService(StrategyRepository strategyRepository, StrategyFactoryRegistry strategyFactoryRegistry, ChartGenerator chartGenerator, @Qualifier("finnhubClient") PriceClient priceClient, CompanySearch companySearch, DefaultPricingContext defaultPricingContext) {
        this.strategyRepository = strategyRepository;
        this.strategyFactoryRegistry = strategyFactoryRegistry;
        this.chartGenerator = chartGenerator;
        this.priceClient = priceClient;
        this.companySearch = companySearch;
        this.defaultPricingContext = defaultPricingContext;
    }

    public List<Company> generateListOfCompanies(String keySearch) {
        return companySearch.getCompanies(keySearch);
    }

    public List<ChartPoint> processPreviewChart(Request request, double spotPrice) {
       OptionStrategy domainStrategy = mapRequestToOptionStrategy(request, spotPrice);
       LocalDate evaluationDate = domainStrategy.getOptionLegs().stream().min(Comparator.comparing(OptionLeg::expiryDate)).get().expiryDate();
       return chartGenerator.draw(spotPrice, domainStrategy.getOptionLegs(), new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility(), evaluationDate));
    }

    public double getSpotPrice(String ticker){
        double price = priceClient.getStockPrice(ticker);
        if (price == -1) {
            throw new APILimitExceededException("Probably API limit exceeded. See you tomorrow!");
        }
        return price;
    }

    public OptionStrategy mapRequestToOptionStrategy(Request request, double spotPrice){
        return strategyFactoryRegistry.mapToDomain(request, spotPrice);
    }

    public PortfolioStrategy createStrategy(Request request) {
        double spotPrice = getSpotPrice(request.getSelectedCompany().ticker());
        OptionStrategy domainStrategy = mapRequestToOptionStrategy(request, spotPrice);
        return mapToPortfolio(domainStrategy, spotPrice, request.getSelectedCompany());
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

    public PortfolioStrategy saveStrategyToPortfolio(PortfolioStrategy portfolioStrategy){
        return strategyRepository.save(portfolioStrategy);
    }
}