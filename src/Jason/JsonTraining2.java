package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTraining2 {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String libraryJson = "{"
                + "\"nazwa\": \"Biblioteka Miejska\","
                + "\"adres\": { \"miasto\": \"Warszawa\", \"ulica\": \"Marszałkowska 1\" },"
                + "\"ksiazki\": [\"Wiedźmin\", \"Lalka\", \"Dżuma\"]"
                + "}";
        try {
            JsonNode jsonNode = mapper.readTree(libraryJson);
            String miasto = jsonNode.get("adres").get("miasto").asText();
            System.out.println(miasto);
            if(jsonNode.get("ksiazki").isArray()){
                System.out.println("Węzeł książki jest tablicą!");
                for(JsonNode jn: jsonNode.get("ksiazki")){
                    System.out.println(jn.asText());
                }
            }
            else {
                System.out.println("Węzeł książki NIE jest tablicą!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
