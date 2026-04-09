package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonTraining5 {
    private final static String url = "https://jsonplaceholder.typicode.com/users";
    private final static HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
    public static void main(String[] args){
        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if(root.isArray()){
                for(JsonNode node : root){
                    if(node.path("id").asInt() > 5){
                        System.out.println(node.path("name").asText());
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
