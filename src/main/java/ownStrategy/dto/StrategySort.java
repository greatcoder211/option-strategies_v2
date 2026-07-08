package ownStrategy.dto;
public record StrategySort(
    String field,      // np. "ticker", "expiry", "score"
    String direction  // "ASC" lub "DESC"
){}
