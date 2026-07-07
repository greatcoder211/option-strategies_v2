package ownStrategy.logic.mapper;

import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.strategy.OptionStrategy;

public interface StrategyFactory<T extends Request> {
    // 1. Zbuduj czysty obiekt domenowy z DTO
    OptionStrategy create(T request);
    // 2. Przedstaw się - powiedz Springowi, jaki typ obsługujesz
    Class<T> getSupportedType();
}