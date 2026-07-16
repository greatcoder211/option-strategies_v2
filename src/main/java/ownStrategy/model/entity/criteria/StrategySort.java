package ownStrategy.model.entity.criteria;
public record StrategySort(
    String field,      // np. "ticker", "expiry", "score"
    String direction  // "ASC" lub "DESC"
){}
