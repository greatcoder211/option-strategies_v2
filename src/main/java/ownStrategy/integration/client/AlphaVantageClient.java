package ownStrategy.integration.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.exception.KeyWordException;
import ownStrategy.exception.TickerNotFoundException;
import ownStrategy.integration.contracts.ClientContracts;
import ownStrategy.logic.network.CompanySearchClient;
import ownStrategy.logic.network.PriceClient;
import ownStrategy.model.entity.portfolio.Company;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class AlphaVantageClient implements PriceClient, CompanySearchClient {
    private final String api_key;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public AlphaVantageClient(@Value("${integration.alphavantage.api.key}") String api_key, ObjectMapper objectMapper) {
        this.api_key = api_key;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
    }

    private ClientContracts.ApiConnectionResponse connectWithApi(String url){
        try{
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            long requestTime = System.currentTimeMillis();
            System.out.println("API CALL at: " + requestTime + " ms");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            if (rootNode.has("Error Message") || rootNode.has("Note") || rootNode.has("Information") || rootNode.isEmpty()) {
                throw new APILimitExceededException("API Limit exceeded or wrong symbol");
            }
            return new ClientContracts.ApiConnectionResponse(rootNode, jsonResponse);
        } catch (TickerNotFoundException | KeyWordException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API Connection failed: " + e.getMessage());
        }
    }

    @Override
    @RateLimiter(name = "alphaVantageLimit")
    public double getStockPrice(String symbol){
        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, api_key);
        ClientContracts.ApiConnectionResponse apiResponse = connectWithApi(url);
        JsonNode globalQuote = apiResponse.rootNode().path("Global Quote");
        if (globalQuote.isMissingNode()||!globalQuote.has("05. price")) {
            System.err.println("RESPONSE: " + apiResponse.jsonResponse());
            return -1.0;
        }
        String priceStr = globalQuote.get("05. price").asText();
        return Double.parseDouble(priceStr);
    }

    @Override
    @RateLimiter(name = "alphaVantageLimit")
    public List<Company> getCompanies(String keySearch){
        String encodedKeywords = URLEncoder.encode(keySearch, StandardCharsets.UTF_8);
        String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + encodedKeywords + "&apikey=" + api_key;
        ClientContracts.ApiConnectionResponse apiResponse = connectWithApi(url);
        JsonNode matches = apiResponse.rootNode().path("bestMatches");
        if (matches.isEmpty() || !matches.isArray()) {
            throw new TickerNotFoundException(keySearch);
        }
        List<Company> results = new ArrayList<>();
        for (JsonNode node : matches) {
            results.add(new Company(
                    node.path("1. symbol").asText(),
                    node.path("2. name").asText()
            ));
        }
        return results;
    }
}
// HttpClient jest thread-safe, trzymamy jedną instancję (optymalizacja)