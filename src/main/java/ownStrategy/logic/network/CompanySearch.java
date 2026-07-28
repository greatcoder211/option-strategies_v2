package ownStrategy.logic.network;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.exception.KeyWordException;
import ownStrategy.model.SearchHistory;
import ownStrategy.repository.SearchHistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanySearch {

    private final CompanySearchClient companySearchClient;
    private final SearchHistoryRepository searchHistoryRepository;

    public CompanySearch(@Qualifier("finnhubClient") CompanySearchClient companySearchClient, SearchHistoryRepository searchHistoryRepository) {
        this.companySearchClient = companySearchClient;
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @RateLimiter(name = "alphaVantageLimit")
    public List<Company> getCompanies(String keySearch) {
        if (keySearch.length() < 2) {
            throw new KeyWordException();
        }
        //sprawdzamy czy już jest w historii
        Optional<SearchHistory> cached = searchHistoryRepository.findByKeyword(keySearch);
        if (cached.isPresent()) {
            System.out.println(">>> MongoDB: Database data from previous searches");
            return cached.get().getCompanies();
        }
        List<Company> resultCompanies = companySearchClient.getCompanies(keySearch);
        if (!resultCompanies.isEmpty()) {
            SearchHistory searchHistory = new SearchHistory(keySearch, resultCompanies.size());
            searchHistory.setCompanies(resultCompanies);
            searchHistoryRepository.save(searchHistory);
            System.out.println(">>> MongoDB: Results saved for a symbol: '" + keySearch + "'");
        }
          return resultCompanies;
    }
}