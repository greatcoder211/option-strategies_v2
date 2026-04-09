package ownStrategy.logic.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ownStrategy.dto.CompanyDTO;
import ownStrategy.exceptions.KeyWordException;
import ownStrategy.exceptions.TickerNotFoundException;
import ownStrategy.model.SearchHistory;
import ownStrategy.repository.SearchHistoryRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class TickerSearch {

    private static final String API_KEY2 = "${ALPHAVANTAGE_API_KEY}";
    private static final HttpClient client2 = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final ObjectMapper mapper2 = new ObjectMapper();
/*
    private final SearchHistoryRepository repository;
    private final String apiKey;
    public TickerSearch(SearchHistoryRepository repository, @Value("${alphavantage.api.key}")apiKey){
        this.repository = repository;
        this.apiKey = API_KEY;
    }

 */
// 1. Pola są teraz instancyjne (bez static) i finalne
    private final String API_KEY;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final SearchHistoryRepository repository;

    // 2. Poprawny konstruktor - Spring wstrzykuje tu wszystko, czego potrzebujemy
    public TickerSearch(SearchHistoryRepository repository, @Value("${ALPHAVANTAGE_API_KEY}") String API_KEY) {
        this.repository = repository;
        this.API_KEY = API_KEY;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }
    // Metoda zwraca wybrany Symbol (String) albo null, jak się nie uda
    public List<CompanyDTO> Companies(String key){


        Optional<SearchHistory> cached = repository.findByKeyword(key);
        if (cached.isPresent()) {
            System.out.println(">>> MongoDB: Real database data");
            return cached.get().getCompanies();
        }

        if ("test".equalsIgnoreCase(key)) {
            SearchHistory history = new SearchHistory("TEST_CONNECTION", 7);
            repository.save(history); // Tu dzieje się magia zapisu
            System.out.println(">>> MongoDB: Dokument wysłany do chmury!");
            return List.of(new CompanyDTO("TEST", "Testowa Firma w Chmurze", "Poland"));
        }

        String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + key + "&apikey=" + API_KEY;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode matches = root.path("bestMatches");

            // TU RZUCAMY WYJĄTEK - zamiast "continue" i pętli while
            if (matches.isEmpty() || !matches.isArray()) {
                throw new TickerNotFoundException(key);
            }
            if(key.length() < 2){
                throw new KeyWordException();
            }
            List<CompanyDTO> results = new ArrayList<>();
            for (JsonNode node : matches) {
                results.add(new CompanyDTO(
                        node.path("1. symbol").asText(),
                        node.path("2. name").asText(),
                        node.path("4. region").asText()
                ));
            }
            if(!results.isEmpty()){
                SearchHistory history2 = new SearchHistory(key, results.size());
                history2.setCompanies(results);
                repository.save(history2);
                System.out.println(">>> MongoDB: Results saved for: " + key);
            }
            return results;
            }
        catch (TickerNotFoundException | KeyWordException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("API Connection failed: " + e.getMessage());
        }
    }

    public static String Ticker(Scanner scanner) {
        System.out.println("Enter the name of the company: ");
        while(true){
            String keyword = "";
            while (keyword.isEmpty()) {
                if (scanner.hasNextLine()) {
                    keyword = scanner.nextLine().trim();
                }
            }
            String encodedKeyword = keyword.replace(" ", "%20");
            String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + encodedKeyword + "&apikey=" + API_KEY2;

            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
                HttpResponse<String> response = client2.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode root = mapper2.readTree(response.body());
                JsonNode matches = root.path("bestMatches");

                if (matches.isEmpty() || !matches.isArray()) {
                    System.out.println("No companies found for: " + keyword + " Try again!");
                    continue;
                }

                int totalMatches = matches.size();
                boolean showAll = false; // Flaga: czy pokazać wszystko?

                // Pętla wyboru - pozwala odświeżyć listę po wybraniu "Pokaż więcej"
                while (true) {
                    // Dynamiczny limit: albo 5, albo wszystko
                    int limit = showAll ? totalMatches : Math.min(totalMatches, 5);

                    System.out.println("\n--- Results (" + limit + " out of " + totalMatches + ") ---");

                    for (int i = 0; i < limit; i++) {
                        JsonNode company = matches.get(i);
                        String symbol = company.path("1. symbol").asText();
                        String name = company.path("2. name").asText();
                        String region = company.path("4. region").asText();
                        String currency = company.path("8. currency").asText();
                        System.out.println("[" + (i + 1) + "] " + symbol + " - " + name + " (" + region + ", " + currency + ")");
                    }

                    // Opcje sterowania
                    System.out.println("--------------------------------");
                    if (!showAll && totalMatches > 5) {
                        System.out.println("[0] Show more results...");
                    }
                    System.out.println("[X] Cancel and search again");
                    System.out.print("Choose your option: ");

                    String input = scanner.nextLine().trim();

                    // Obsługa "Pokaż więcej"
                    if (input.equals("0") && !showAll && totalMatches > 5) {
                        showAll = true; // Zmieniamy flagę
                        continue;       // I kręcimy pętlę od nowa z pełną listą
                    }

                    // Obsługa wyjścia
                    if (input.equalsIgnoreCase("X")) {
                        System.out.println("So, enter the company again: ");
                        break;
                    }

                    // Obsługa wyboru numerka
                    try {
                        int choice = Integer.parseInt(input);
                        if (choice >= 1 && choice <= limit) {
                            return matches.get(choice - 1).path("1. symbol").asText();
                        } else {
                            System.out.println("Wrong number. Choose from the list!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error. Enter a number, '0' lub 'X'.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}