package ownStrategy.dto;
public record SortDTO (
    String field,      // np. "ticker", "expiry", "score"
    String direction  // "ASC" lub "DESC"
){}
