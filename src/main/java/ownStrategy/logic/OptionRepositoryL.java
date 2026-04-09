package ownStrategy.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ownStrategy.legacy.oPattern.Game;
import java.io.File;

public class OptionRepositoryL {
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode loadGameJson() throws Exception {
        return mapper.readTree(new File("data/game.json"));
    }

    public void saveGame(Game game) {
        game.saveToJSon();
    }
}