package ownStrategy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;

import java.util.List;

public interface StrategyRepository extends MongoRepository<PortfolioStrategy, String> {
    List<PortfolioStrategy> findByPortfolioId(Integer portfolioID);
    List<PortfolioStrategy> findByUserId(String userID);
    List<PortfolioStrategy> findByUsername(String username);
}
