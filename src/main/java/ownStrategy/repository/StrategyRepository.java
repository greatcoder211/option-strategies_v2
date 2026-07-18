package ownStrategy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;

import java.util.List;

public interface StrategyRepository extends MongoRepository<PortfolioStrategy, String> {
    List<PortfolioStrategy> findByPortfolioStrategyID(String portfolioStrategyID);
    List<PortfolioStrategy> findByUserId(String userId);
}
