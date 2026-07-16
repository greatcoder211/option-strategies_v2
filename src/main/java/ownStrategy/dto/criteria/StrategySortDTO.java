package ownStrategy.dto.criteria;

public record StrategySortDTO(
    String field,      // np. "ticker", "expiry", "score"
    String direction  // "ASC" lub "DESC"
){}
