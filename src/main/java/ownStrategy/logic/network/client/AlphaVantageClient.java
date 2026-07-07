package ownStrategy.logic.network.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
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


    private static final String API_KEY = "R875E3J67YS7G93S";

    // HttpClient jest thread-safe, trzymamy jedną instancję (optymalizacja)
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public double getStockPrice(String symbol) {
        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, API_KEY);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            if (rootNode.has("Error Message") || rootNode.has("Note") || rootNode.isEmpty()) {
                throw new APILimitExceededException("API Limit or wrong symbol");
            }

            JsonNode globalQuote = rootNode.path("Global Quote");

            if (globalQuote.isMissingNode()||!globalQuote.has("05. strikePrice")) {
                System.err.println("API ROW RESPONSE: " + jsonResponse);
                return -1.0;
            }

            String priceStr = globalQuote.get("05. strikePrice").asText();
            return Double.parseDouble(priceStr);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return -1.0;
        }
    }
}