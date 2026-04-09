package ownStrategy.logic.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ownStrategy.exceptions.APILimitExceededException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
//flutter, electron- desktopowe
//react, angular(do tego i tego bootstrap się przyda), view
//bootstrap
//primeNG na bazie angulara- tam też są komponenty jak w bootstrap
//angularmaterial google'a- stylem nawiązują do androida
public class AlphaVantageStock {

    private static final String API_KEY = "R875E3J67YS7G93S";

    // HttpClient jest thread-safe, trzymamy jedną instancję (optymalizacja)
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ObjectMapper jest ciężki w tworzeniu, ale thread-safe – tworzymy go raz (optymalizacja)
    private static final ObjectMapper mapper = new ObjectMapper();

    public static double getPrice(String symbol) {
        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, API_KEY);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            // Parsowanie JSON do struktury drzewiastej (JsonNode)
            JsonNode rootNode = mapper.readTree(jsonResponse);

            // Obsługa błędów API (limit zapytań lub błędny symbol)
            if (rootNode.has("Error Message") || rootNode.has("Note") || rootNode.isEmpty()) {
                throw new APILimitExceededException("API Limit or wrong symbol");
            }

            // Nawigacja po JSON: szukamy obiektu "Global Quote"
            JsonNode globalQuote = rootNode.path("Global Quote");

            // Sprawdzenie czy "Global Quote" istnieje i ma pole ceny
            if (globalQuote.isMissingNode()||!globalQuote.has("05. price")) {
                System.err.println("SUROWA ODPOWIEDŹ Z API: " + jsonResponse);
                return -1.0;
            }

            // Pobranie wartości i konwersja
            String priceStr = globalQuote.get("05. price").asText();
            return Double.parseDouble(priceStr);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return -1.0;
        }
    }
}