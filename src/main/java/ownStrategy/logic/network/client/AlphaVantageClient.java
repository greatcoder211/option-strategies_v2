package ownStrategy.logic.network.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.logic.network.MarketDataClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
@Data
@Component
public class AlphaVantageClient implements MarketDataClient {
    private final String api_key;
    // HttpClient jest thread-safe, trzymamy jedną instancję (optymalizacja)
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public AlphaVantageClient(@Value("${alphavantage.api.key}") String api_key) {
        this.api_key = api_key;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public double getStockPrice(String symbol) {
        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, api_key);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                                            .uri(URI.create(url))
                                            .GET()
                                            .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            if (rootNode.has("Error Message") || rootNode.has("Note") || rootNode.isEmpty()) {
                throw new APILimitExceededException("API Limit or wrong symbol");
            }
            JsonNode globalQuote = rootNode.path("Global Quote");
            if (globalQuote.isMissingNode()||!globalQuote.has("05. price")) {
                System.err.println("RESPONSE: " + jsonResponse);
                return -1.0;
            }
            String priceStr = globalQuote.get("05. price").asText();
            return Double.parseDouble(priceStr);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return -1.0;
        }
    }
}