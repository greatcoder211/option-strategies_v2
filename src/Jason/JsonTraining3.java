package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
class Jason{

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String url = "http://api.nbp.pl/api/exchangerates/rates/a/eur/?format=json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static double findMid (){
        double d = 0;
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            JsonNode node = mapper.readTree(jsonResponse);
            for(JsonNode jn: node.path("rates")){
                d = jn.get("mid").asDouble();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return d;
    }
}

public class JsonTraining3 {
    public static void main(String[] args) {
        System.out.println("The value is: " + Jason.findMid());
    }
}
