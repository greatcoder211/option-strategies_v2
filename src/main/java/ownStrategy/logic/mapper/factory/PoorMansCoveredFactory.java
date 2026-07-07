package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.strategyPanel.PoorMansCoveredRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.diagonal.PoorMansCovered;

public class PoorMansCoveredFactory implements StrategyFactory<PoorMansCoveredRequest> {

    @Override
    public OptionStrategy create(PoorMansCoveredRequest request) {
        return new PoorMansCovered(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getStrikePrices(),
                request.getTradeDate(),
                request.getShortExpiryDate(),
                request.getLongExpiryDate(),
                request.getSpotPrice()
        );
    }

    @Override
    public Class<PoorMansCoveredRequest> getSupportedType() {
        return PoorMansCoveredRequest.class;
    }
}
