package Jason;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTraining1 {
    public static void main(String[] args) {
        String carJson = "{ \"marka\": \"Tesla\", \"model\": \"Model S\", \"rok\": 2022, \"czyElektryk\": true }";
        ObjectMapper mapper = new ObjectMapper();
        String marka = null;
        String model = null;
        int rok = 0;
        boolean czy = false;
        try {
            JsonNode jsonNode = mapper.readTree(carJson);
            marka = jsonNode.findPath("marka").asText();
            model = jsonNode.findPath("model").asText();
            rok = jsonNode.findPath("rok").asInt();
            czy =  jsonNode.findPath("czyElektryk").asBoolean();
            System.out.println("Samochód " + marka + " z roku " + rok + ". Czy elektryczny? " + czy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
