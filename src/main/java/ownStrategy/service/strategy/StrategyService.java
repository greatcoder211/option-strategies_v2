package ownStrategy.service.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.config.DefaultPricingContext;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.Status;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.finance.StrategyCalculator;
import ownStrategy.logic.network.MarketDataClient;
import ownStrategy.model.*;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.portfolio.OptionLeg;
import ownStrategy.repository.StrategyRepository;
import ownStrategy.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StrategyService {
    private final StrategyRepository strategyRepository;
    private final UserRepository userRepository;
    private final DefaultPricingContext defaultPricingContext;
    private final ChartGenerator chartGenerator;
    private final MarketDataClient marketDataClient;

    public StrategyService(StrategyRepository strategyRepository, UserRepository userRepository,
                           ChartGenerator chartGenerator, MarketDataClient marketDataClient,
                           DefaultPricingContext defaultPricingContext) {
        this.strategyRepository = strategyRepository;
        this.userRepository = userRepository;
        this.chartGenerator = chartGenerator;
        this.marketDataClient = marketDataClient;
        this.defaultPricingContext = defaultPricingContext;
    }

    //-- BIG THINGS OUGHT TO HAPPEN --
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

    @Transactional
    public void closeExpiredStrategies() {
        List<PortfolioStrategy> portfolio = strategyRepository.findAll();
        List<PortfolioStrategy> strategiesToClose = new ArrayList<>();
        for (PortfolioStrategy portfolioStrategy : portfolio) {
            if (hasExpiredLeg(portfolioStrategy)) {
                portfolioStrategy.setStatus(Status.CLOSED);
                strategiesToClose.add(portfolioStrategy);
            }
        }
        if (!strategiesToClose.isEmpty()) {
            strategyRepository.saveAll(strategiesToClose);
        }
    }

    public boolean hasExpiredLeg(PortfolioStrategy portfolioStrategy) {
        for (OptionLeg optionLeg : portfolioStrategy.getOptionLegs()) {
            if (optionLeg.expiryDate().isBefore(LocalDate.now())) {
                return true;
            }
        }
        return false;
    }

    public List<PortfolioStrategy> getPortfolioByUserId(String UserId) {
        if (userRepository.findById(UserId).isPresent()) {
            return strategyRepository.findByUserId(UserId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
        }
    }

    public List<PortfolioStrategy> getAllPortfolios() {
        List<PortfolioStrategy> portfolioStrategies = strategyRepository.findAll();
        return !portfolioStrategies.isEmpty() ? portfolioStrategies : null;
    }

    public Optional<PortfolioStrategy> getStrategyByStrategyId(String portfolioStrategyId) {
        return strategyRepository.findById(portfolioStrategyId);
    }

    public List<PortfolioStrategy> getPortfolioByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return strategyRepository.findByUserId(user.get().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + username);
        }
    }

    public void deleteUserPortfolio(String userId) {
        Optional<PortfolioStrategy> portfolioStrategy = strategyRepository.findById(userId);
        portfolioStrategy.ifPresent(strategyRepository::delete);
    }
}

/*  ---do naklepania---
    - sprawdzenie i ewentualna zmiana statusu strategii(wykonywane codziennie na początku dnia w produkcji)
 */
//metody w kolejności wywoływania