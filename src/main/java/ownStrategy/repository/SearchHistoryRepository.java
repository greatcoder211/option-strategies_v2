package ownStrategy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ownStrategy.model.SearchHistory;

import java.util.Optional;

public interface SearchHistoryRepository extends MongoRepository<SearchHistory, String> {
    Optional<SearchHistory> findByKeyword(String keyword);
}
