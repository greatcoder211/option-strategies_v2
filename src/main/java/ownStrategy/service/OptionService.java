package ownStrategy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.config.DefaultPricingContext;
import ownStrategy.config.StrategiesPage;
import ownStrategy.dto.*;
import ownStrategy.dto.portfolio.PortfolioStrategy;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.logic.PortfolioStrategyFilter;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.WalletFilter;
import ownStrategy.logic.finance.OptionCalculator;
import ownStrategy.logic.finance.StrategyCalculator;
import ownStrategy.logic.mapper.StrategyMapper;
import ownStrategy.logic.network.MarketDataClient;
import ownStrategy.logic.network.TickerSearch;
import ownStrategy.model.*;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.repository.StrategyRepository;
import ownStrategy.repository.UserRepository;
import ownStrategy.model.strategy.CallPutStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.sort;

@Service
public class OptionService {
    private final StrategyRepository strategyRepo;
    private final UserRepository userRepo;
    private final MongoTemplate mongoTemplate;
    private final PortfolioStrategyFilter portfolioStrategyFilter;
    private final DefaultPricingContext defaultPricingContext;

    //---GREAT REFACTORIZATION---
    private final ChartGenerator chartGenerator;
    private final StrategyMapper strategyMapper;
    private final MarketDataClient marketDataClient;
    private final TickerSearch tickerSearch;

    public OptionService(StrategyRepository strategyRepo, UserRepository userRepo, MongoTemplate mongoTemplate, PortfolioStrategyFilter portfolioStrategyFilter,
                         ChartGenerator chartGenerator, StrategyMapper strategyMapper, MarketDataClient marketDataClient,
                         DefaultPricingContext defaultPricingContext, TickerSearch tickerSearch) {
        this.strategyRepo = strategyRepo;
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
        this.portfolioStrategyFilter = portfolioStrategyFilter;
        this.chartGenerator = chartGenerator;
        this.marketDataClient = marketDataClient;
        this.defaultPricingContext = defaultPricingContext;
        this.tickerSearch = tickerSearch;
    }
//-- BIG THINGS OUGHT TO HAPPEN --
    public PortfolioStrategy createStrategy(Request request) {
        double spotPrice = marketDataClient.getStockPrice(request.getTicker());
        OptionStrategy domainStrategy = strategyMapper.mapToDomain(request);
        return mapToPortfolio(domainStrategy, spotPrice, request.getTicker());
    }

    public PortfolioStrategy mapToPortfolio(OptionStrategy domainStrategy, double spotPrice, String ticker) {
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
                .ticker(ticker)
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

    public List<PortfolioStrategy> getUserStrategies(String UserId) {
        if(userRepo.findById(UserId).isPresent()){
            return strategyRepo.findByUserID(UserId);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
        }
    }

    public double calculatePnL(List<OptionLeg> optionLegs, double entrySpotPrice, String ticker) {
        double simulatedSpotPrice = marketDataClient.getStockPrice(ticker);
        return StrategyCalculator.calculatePnL(optionLegs, entrySpotPrice, simulatedSpotPrice, new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility()));
    }

    public List<ChartPoint> makeChart(List<OptionLeg> optionLegs, Double spotPrice) {
        return chartGenerator.draw(spotPrice, optionLegs);
    }

    public ChartPoint makeCurrentPriceMarker(List<OptionLeg> optionLegs, double entryPrice, String ticker) {
        double currentPrice = marketDataClient.getStockPrice(ticker);
        double currentProfit = StrategyCalculator.calculatePnL(optionLegs, entryPrice, currentPrice, new PricingContext(defaultPricingContext.getRiskFreeRate(), defaultPricingContext.getVolatility()));
        return new ChartPoint(currentPrice, currentProfit);
    }
    // --- DEBUG ---

    public int getChoice(String line){
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
    public List<TheWallet> getAllTheseWallets(){
        return strategyRepo.findAll();
    }

    public void cleanUp(String id) {
        Optional<TheWallet> wallet = strategyRepo.findById(id);
        if(wallet.isPresent()){
            strategyRepo.delete(wallet.get());
        }
    }

    public Optional<TheWallet> getWallet(String id){
        if(strategyRepo.findById(id).isPresent()){
            return strategyRepo.findById(id);
        }
        return Optional.empty();
    }

    public void saveToRepo(TheWallet wallet) {
        wallet.setDate(LocalDateTime.now());
        wallet.setStatus(Status.OPEN);
        strategyRepo.save(wallet);
    }

    public void checkUser(String UserId, TheWallet wallet) {
        if(userRepo.findById(UserId).isPresent()){
            wallet.setUserID(UserId);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
        }
    }

    public void saveUser(User user) {
        if(!user.getEmail().contains("@")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email must contain '@'");
        }
        else if(!user.getEmail().toLowerCase().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email cannot contain capital letters");
        }
        userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void firstWalletCheck(TheWallet wallet, SpreadStrategy strategy) {
        if(strategy.getType() == null)
            strategy.setType(OptionType.NA);
        if(wallet.getType() == null && !strategy.getCP())
            wallet.setType(OptionType.NA);
        if(wallet.getLegs().size() != strategy.getLegs().size())
            throw new  ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong spreads entered. Only the number allowed");
        else if(wallet.getType() != OptionType.NA && !strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong optiontype. For this type of strategy you can only enter 'NA' Non-Applicable");
        else if(wallet.getType() == OptionType.NA && strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NA doesn't apply here. You have to choose a CALL or PUT variant for this type of strategy.");
    }

    @Transactional
    public void closePosition() {
        List<TheWallet> wallets = strategyRepo.findAll();
        for (TheWallet wallet : wallets) {
            if(wallet.getExpiry().isBefore(LocalDate.now())){
                wallet.setStatus(Status.CLOSED);
                strategyRepo.save(wallet);
            }
        }
    }

//logikę tego też w teorii trzebaby wynieść do osobnego interfejsu funkcjonalnego(vielleicht fabryka), ale już mi się nie chce z tym tak pierdolić(no nie), tym bardziej, że dużo iwęcej if-ów już tu nie wejdzie(no ile można tworzyć filtrów)
    public Page<PortfolioStrategy> filter(Filter filter) {
        List<Criteria> criteriaList = new ArrayList<>();

        if(filter.getCreatedAtFrom() != null || filter.getCreatedAtTo() != null){
            criteriaList.add(portfolioStrategyFilter.filterByCreatedAtRange(filter.getCreatedAtFrom(), filter.getCreatedAtTo()));
        }

        if (filter.getPosition() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByPosition(filter.getPosition()));
        }

        if (filter.getStrategies() != null && !filter.getStrategies().isEmpty()) {
            criteriaList.add(portfolioStrategyFilter.filterByStrategies(filter.getStrategies()));
        }

        if (filter.getCompaniesConcat() != null && !filter.getCompaniesConcat().isEmpty()) {
            criteriaList.add(portfolioStrategyFilter.filterByCompanies(filter.getCompaniesConcat()));
        }

        if(filter.getIsCallPutStrategy() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByCallPutStrategies(filter.getIsCallPutStrategy(), filter.getCallOrPut(), filter.getOptionType()));
        }

        if(filter.getIsTraditional() != null){
            criteriaList.add(portfolioStrategyFilter.filterByTraditionalStrategies(filter.getIsTraditional()));
        }

        if(filter.getIsBrokenLeg() != null){
            criteriaList.add(portfolioStrategyFilter.filterByBrokenLegs(filter.getIsBrokenLeg()));
        }

        if (filter.getTradeDateFrom() != null || filter.getTradeDateTo() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByTradeDateRange(filter.getTradeDateFrom(), filter.getTradeDateTo()));
        }

        if (filter.getExpiryDateFrom() != null || filter.getExpiryDateTo() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByExpiryDateRange(filter.getExpiryDateFrom(), filter.getExpiryDateTo()));
        }

        if (filter.getStatus() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByStatus(filter.getStatus()));
        }
        //złączenie wszystkich kryteriów
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Sort sort = sort(filter.getStrategySortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        query.with(pageable);
        List<TheWallet> list = mongoTemplate.find(query, TheWallet.class);

        return PageableExecutionUtils.getPage(list, pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), PortfolioStrategy.class));
    }

    public void APILimitExceededCheck(String companyTicker) {
        double companyPrice = marketDataClient.getStockPrice(companyTicker);
        if(companyPrice == -1)
            throw new APILimitExceededException("Probably API limit exceeded. See you tomorrow!");
    }
/*  ---do naklepania---
    - sprawdzenie i ewentualna zmiana statusu strategii(wykonywane codziennie na początku dnia w produkcji)
 */

}
//metody w kolejności wywoływania