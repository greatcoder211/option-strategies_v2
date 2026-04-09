package ownStrategy.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import ownStrategy.model.TheWallet;

import java.util.List;

public interface StrategyRepository extends MongoRepository<TheWallet, String> {
    List<TheWallet> findByUserID(String userID);
 //   List<TheWallet> getFilteredAndSortedTrades(Criteria criteria, Sort sort);

}
