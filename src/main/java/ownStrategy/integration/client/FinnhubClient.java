package ownStrategy.integration.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.exception.InvalidAPITokenException;
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
public class FinnhubClient implements PriceClient, CompanySearchClient {

    private final String api_token;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FinnhubClient(@Value("${integration.finnhub.api.token}") String api_token, ObjectMapper objectMapper) {
        this.api_token = api_token;
        this.httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(10))
                                    .build();
        this.objectMapper = objectMapper;
    }

    private ClientContracts.ApiConnectionResponse connectWithApi(String url){
        try{HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            long requestTime = System.currentTimeMillis();
            System.out.println("API CALL at: " + requestTime + " ms");
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = httpResponse.body();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            if (httpResponse.statusCode() == 429) {
                throw new APILimitExceededException("Rate limit exceeded. Status 429.");
            }
            else if (httpResponse.statusCode() == 401) {
                throw new InvalidAPITokenException("Invalid API token. Status 401.");
            }
            else if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Unexpected API error. Status: " + httpResponse.statusCode());
            }
            return new ClientContracts.ApiConnectionResponse(rootNode, jsonResponse);
        } catch (TickerNotFoundException | KeyWordException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API Connection failed: " + e.getMessage());
        }
    }

    @Override
    @RateLimiter(name = "finnHubLimit")
    public double getStockPrice(String symbol){
        String url = String.format("https://www.finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, api_token);
        ClientContracts.ApiConnectionResponse apiResponse = connectWithApi(url);
        if (apiResponse.rootNode().path("c").asText().equals("0")) {
            System.err.println("RESPONSE: " + apiResponse.jsonResponse());
            return -1.0;
        }
        String priceStr = apiResponse.rootNode().get("c").asText();
        return Double.parseDouble(priceStr);
    }

    @Override
    @RateLimiter(name = "finnHubLimit")
    public List<Company> getCompanies(String keySearch){
        String encodedKeywords = URLEncoder.encode(keySearch, StandardCharsets.UTF_8);
        String url = String.format("https://www.finnhub.io/api/v1/search?q=%s&token=%s", encodedKeywords, api_token);
        ClientContracts.ApiConnectionResponse apiResponse = connectWithApi(url);
        JsonNode resultCompanies = apiResponse.rootNode().path("result");
        if (resultCompanies.isEmpty() || !resultCompanies.isArray()) {
            throw new TickerNotFoundException(keySearch);
        }
        List<Company> results = new ArrayList<>();
        for (JsonNode node : resultCompanies) {
            results.add(new Company(
                    node.path("symbol").asText(),
                    node.path("description").asText()
            ));
        }
        return results;
    }
}
