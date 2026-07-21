package ownStrategy.logic.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.exception.KeyWordException;
import ownStrategy.exception.TickerNotFoundException;
import ownStrategy.model.SearchHistory;
import ownStrategy.repository.SearchHistoryRepository;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TickerSearch {
    private final String api_key;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final SearchHistoryRepository searchHistoryRepository;

    public TickerSearch(SearchHistoryRepository searchHistoryRepository, @Value("${alphavantage.api.key}") String api_key) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.api_key = api_key;
        this.client = HttpClient.newBuilder()
                                .connectTimeout(Duration.ofSeconds(10))
                                .build();
        this.mapper = new ObjectMapper();
    }

    public List<Company> getCompanies(String key) {
        //żeby nie pukać ponownie pod ten sam adres, sprawdzamy czy już kiedyś nie wykonaliśmy danej kwerendy
        Optional<SearchHistory> cached = searchHistoryRepository.findByKeyword(key);
        if (cached.isPresent()) {
            System.out.println(">>> MongoDB: Real database data");
            return cached.get().getCompanies();
        }
        //"test": do wywalenia niedługo
        if ("test".equalsIgnoreCase(key)) {
            SearchHistory history = new SearchHistory("TEST_CONNECTION", 7);
            searchHistoryRepository.save(history);
            System.out.println(">>> MongoDB: Dokument wysłany do chmury!");
            return List.of(new Company("TEST", "Testowa Firma w Chmurze", "Poland"));
        }
        String encodedKeywords = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + encodedKeywords + "&apikey=" + api_key;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//todo: wywalic
            System.out.println("RAW RESPONSE FROM API: " + response.body());
            JsonNode root = mapper.readTree(response.body());
            JsonNode matches = root.path("bestMatches");

            // TU RZUCAMY WYJĄTEK - zamiast "continue" i pętli while
            if (matches.isEmpty() || !matches.isArray()) {
                throw new TickerNotFoundException(key);
            }
            if (key.length() < 2) {
                throw new KeyWordException();
            }
            List<Company> results = new ArrayList<>();
            for (JsonNode node : matches) {
                results.add(new Company(
                        node.path("1. symbol").asText(),
                        node.path("2. name").asText(),
                        node.path("4. region").asText()
                ));
            }
            if (!results.isEmpty()) {
                SearchHistory history2 = new SearchHistory(key, results.size());
                history2.setCompanies(results);
                searchHistoryRepository.save(history2);
                System.out.println(">>> MongoDB: Results saved for: " + key);
            }
            return results;
        } catch (TickerNotFoundException | KeyWordException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API Connection failed: " + e.getMessage());
        }
    }
}