package ownStrategy.integration.contracts;

import com.fasterxml.jackson.databind.JsonNode;

public class ClientContracts {
    public record ApiConnectionResponse(JsonNode rootNode, String jsonResponse) {}
}
