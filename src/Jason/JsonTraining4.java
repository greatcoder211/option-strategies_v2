package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonTraining4 {
    public static HttpClient client =  HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    public static final String url = "https://jsonplaceholder.typicode.com/users/1";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            System.out.println(root.path("address").path("city").asText());
            System.out.println(String.format("%.2f", root.path("address").path("geo").path("lat").asDouble()) + " " + String.format("%.2f", root.path("address").path("geo").path("lng").asDouble()));
            System.out.println(root.path("wiek").asInt(-1));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
