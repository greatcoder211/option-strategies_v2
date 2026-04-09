package ownStrategy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ownStrategy.model.TheWallet;

import java.util.List;

@Repository
public interface OptionRepository extends MongoRepository<TheWallet,String> {
    List<TheWallet> findByUserID(String userID);
}
