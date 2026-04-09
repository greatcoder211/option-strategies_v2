package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonTraining6 {
    public static ObjectMapper mapper = new ObjectMapper();
    public static final String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=R875E3J67YS7G93S";
    public static HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    public static void main(String[] args) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            String date = root.path("Meta Data").path("3. Last Refreshed").asText();
            long volume = root.path("Time Series (Daily)").path(date).path("5. volume").asLong();
            System.out.println("Dnia " + date + " wolumen wyniósł: " + volume);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
